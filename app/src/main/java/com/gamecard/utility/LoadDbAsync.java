package com.gamecard.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.gamecard.model.GameResponseModel;
import com.gamecard.model.VedioImageLinkModel;
import com.gamecard.view.HomeView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bridgeit on 4/8/16.
 */

public class LoadDbAsync extends AsyncTask<Void, Void, Map<String,Object>> {

    private ArrayList<ApplicationInfo> gameList;
    private Map<String, ApplicationInfo> applicationInfoMap;
    private Map<String, Object> outputMap;
    private ArrayList<String> packageList;
    private ArrayList<String> dbList;
    Context context;
    private Realm realm;
    private final String TAG="LoadDbAsync";

    public LoadDbAsync(Context context){
        this.context=context;
    }

    @Override
    protected Map<String, Object> doInBackground(Void... params) {


        realm = Realm.getInstance(context);
        gameList = new ArrayList<>();
        long start=System.currentTimeMillis();
        applicationInfoMap = ApplicationUtility.getInstallApp(ApplicationUtility.getAllPackages(context));


        packageList = new ArrayList<>(applicationInfoMap.keySet());
        dbList = new ArrayList<>();
        outputMap=new HashMap<>();


        // Build the query looking at all users:
        RealmResults<GameResponseModel> realmResults = realm.where(GameResponseModel.class).equalTo("suggestion",false).findAll();

        genrateList(realmResults);

        long end=System.currentTimeMillis();
        Log.i(TAG, "doInBackground: **** "+(end-start));
        return outputMap;
    }


    private void genrateList(RealmResults<GameResponseModel> realmResults) {

        //display game in the grid view
        for (GameResponseModel gameResponseModel : realmResults) {
            if (gameResponseModel.getIsgame()) {
                Log.i(TAG, gameResponseModel.getPackagename()+" ++++ "+gameResponseModel.getJsonImageVedioLink());
                VedioImageLinkModel vedioImageLinkModel=new Gson().fromJson(gameResponseModel.getJsonImageVedioLink(),VedioImageLinkModel.class);
                if (applicationInfoMap.containsKey(gameResponseModel.getPackagename()))
                    gameList.add(applicationInfoMap.get(gameResponseModel.getPackagename()));
            }
            dbList.add(gameResponseModel.getPackagename());
        }

        //Find the difference between database results  and install application
        packageList.removeAll(dbList);

        outputMap.put(HomeView.NEW_PACKAGE_LIST,packageList);
        outputMap.put(HomeView.GAME_LIST,gameList);
        outputMap.put(HomeView.APPLICATION_INFO_MAP,applicationInfoMap);

        realm.close();

    }
}

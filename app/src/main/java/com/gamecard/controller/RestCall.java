package com.gamecard.controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.gamecard.adapter.AdapterVideoDisplay;
import com.gamecard.callback.CallbackGameChecker;
import com.gamecard.callback.CallbackRestResponse;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.utility.AppController;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.PackageModel;
import com.gamecard.model.RestResponseModel;
import com.gamecard.utility.AppController;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bridgeit on 12/7/16.
 */

public class RestCall {

    @Inject
    Retrofit retrofit;

    public RestCall(Context context){
        ((AppController)context.getApplicationContext()).getmNetComponent().inject(this);
        ((AppController)context.getApplicationContext()).getnetComponent1().inject(this);
    }

  /*  public static void isGame(final ApplicationInfo applicationInfo, final CallbackGameChecker callbackGameChecker){
        GameResponseInterface listInterface=ApiClient.getClient().create(GameResponseInterface.class);
        Call<GameResponseModel> userInfoCall=listInterface.getGameResponse(applicationInfo.packageName);
        userInfoCall.enqueue(new Callback<GameResponseModel>() {
            @Override
            public void onResponse(Call<GameResponseModel> call, Response<GameResponseModel> response) {
                GameResponseModel gameResponseModel = response.body();
                if(gameResponseModel.getIsgame())
                    callbackGameChecker.applicationIsGame(applicationInfo,gameResponseModel);
                else
                    callbackGameChecker.applicationIsNotGame(applicationInfo);

            }
            @Override
            public void onFailure(Call<GameResponseModel> call, Throwable t) {
                callbackGameChecker.onError(t);
            }
        });
    }*/

    public void sendPackageList(PackageModel packageModel/*, final CallbackRestResponse callbackRestResponse*/){
        GameResponseInterface listInterface=retrofit.create(GameResponseInterface.class);
        Call<RestResponseModel> getPackageModel=listInterface.getResponseList(packageModel);
        getPackageModel.enqueue(new Callback<RestResponseModel>() {
            @Override
            public void onResponse(Call<RestResponseModel> call, Response<RestResponseModel> response) {
                //callbackRestResponse.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<RestResponseModel> call, Throwable t) {
                //callbackRestResponse.onError(t);
            }
        });
    }





   /* public static class ApiClient {

        public static final String BASE_URL = "http://localhost:8080/GameCard/";
        private static Retrofit retrofit1 = null;


        public static Retrofit getClient() {
            if (retrofit1==null) {
                retrofit1 = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit1;
        }
    }*/

    public void getVideoDisplayList(String packageName, final CallbackRestResponse callback){
        GameResponseInterface listInterface1 = retrofit.create(GameResponseInterface.class);
        final Call<List<VideoDisplayModel>> getVideoList1 = listInterface1.getVideoList(packageName);
        getVideoList1.enqueue(new Callback<List<VideoDisplayModel>>() {
            @Override
            public void onResponse(Call<List<VideoDisplayModel>> call, Response<List<VideoDisplayModel>> response) {
                List<VideoDisplayModel> videoDisplay = response.body();
                String TAG="On restCall getVideoDisplayList";
                if(response.isSuccessful()){
                 callback.onResponse(videoDisplay);
                }else{
                    Log.e(TAG,"error");
                }
            }

            @Override
            public void onFailure(Call<List<VideoDisplayModel>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
package com.gamecard.controller;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.gamecard.callback.CallbackGameChecker;
import com.gamecard.callback.CallbackRestResponse;
import com.gamecard.utility.AppController;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.PackageModel;
import com.gamecard.model.RestResponseModel;
import com.gamecard.utility.AppController;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by bridgeit on 12/7/16.
 */

public class RestCall {

    @Inject
    Retrofit retrofit;

    public RestCall(Context context){
        ((AppController)context.getApplicationContext()).getmNetComponent().inject(this);
    }

    /*public static void isGame(final ApplicationInfo applicationInfo, final CallbackGameChecker callbackGameChecker){
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
}

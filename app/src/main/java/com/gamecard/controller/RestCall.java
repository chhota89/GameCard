package com.gamecard.controller;

import android.content.pm.ApplicationInfo;

import com.gamecard.callback.CallbackGameChecker;
import com.gamecard.callback.CallbackRestResponse;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.PackageModel;
import com.gamecard.model.RestResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bridgeit on 12/7/16.
 */

public class RestCall {

    public static void isGame(final ApplicationInfo applicationInfo, final CallbackGameChecker callbackGameChecker){
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
    }

    public static void sendPackageList(PackageModel packageModel/*, final CallbackRestResponse callbackRestResponse*/){
        GameResponseInterface listInterface=ApiClient.getClient().create(GameResponseInterface.class);
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

package com.gamecard.controller;

import com.gamecard.model.GameResponseModel;
import com.gamecard.model.PackageModel;
import com.gamecard.model.RestResponseModel;
import com.gamecard.model.VideoDisplayModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by bridgeit on 12/7/16.
 */

public interface GameResponseInterface {

    @GET("gamecard")
    Call<GameResponseModel> getGameResponse(@Query("packagename") String packageName);

    @Headers( "Content-Type: application/json" )
    @POST("package")
    Call<RestResponseModel> getResponseList(@Body PackageModel packageModel);

    @Headers( "Content-Type: application/json" )
    @GET("getVedioList")
    Call<List<VideoDisplayModel>> getVideoList(@Query("packageName") String packageName);
}

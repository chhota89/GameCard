package com.gamecard.callback;

import com.gamecard.model.VideoDisplayModel;

import java.util.List;

/**
 * Created by bridgeit on 1/8/16.
 */

public interface CallbackRestResponse {

    void onResponse(List<VideoDisplayModel> videoResponseModel);

    void onError(Throwable throwable);
}

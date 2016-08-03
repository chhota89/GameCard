package com.gamecard.callback;

import com.gamecard.model.RestResponseModel;

/**
 * Created by bridgeit on 1/8/16.
 */

public interface CallbackRestResponse {
    void onResponse(RestResponseModel responseModel);
    void onError(Throwable throwable);
}

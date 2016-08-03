package com.gamecard.callback;

import com.gamecard.model.GameResponseModel;

/**
 * Created by bridgeit on 1/8/16.
 */

public interface CallBackMqtt {
    void onMessageRecive(GameResponseModel gameResponseModel);
    void onError(Throwable throwable);
}

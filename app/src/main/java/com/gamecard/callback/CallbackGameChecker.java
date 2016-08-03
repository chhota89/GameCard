package com.gamecard.callback;

import android.content.pm.ApplicationInfo;

import com.gamecard.model.GameResponseModel;

/**
 * Created by bridgeit on 12/7/16.
 */

public interface CallbackGameChecker {

    void applicationIsGame(ApplicationInfo applicationInfo, GameResponseModel gameResponseModel);
    void applicationIsNotGame(ApplicationInfo applicationInfo);
    void onError(Throwable throwable);
}

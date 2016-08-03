package com.gamecard.dagger;

import android.app.Application;

import com.gamecard.dagger.component.DaggerNetComponent;
import com.gamecard.dagger.component.NetComponent;
import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.NetModule;

/**
 * Created by bridgeit on 14/7/16.
 */

public class App extends Application {
    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule("http://google.com"))
                .build();
    }

    public NetComponent getmNetComponent() {
        return mNetComponent;
    }
}

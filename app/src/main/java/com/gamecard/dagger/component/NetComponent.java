package com.gamecard.dagger.component;

import com.gamecard.controller.RestCall;
import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.MqttModule;
import com.gamecard.dagger.module.NetModule;
import com.gamecard.view.HomeView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bridgeit on 14/7/16.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(RestCall restCall);

}

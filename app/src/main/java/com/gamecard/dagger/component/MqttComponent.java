package com.gamecard.dagger.component;

import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.MqttModule;
import com.gamecard.dagger.module.NetModule;
import com.gamecard.view.HomeView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bridgeit on 9/8/16.
 */

@Singleton
@Component(modules = {AppModule.class,MqttModule.class})
public interface MqttComponent {
    void inject(HomeView homeView);
}

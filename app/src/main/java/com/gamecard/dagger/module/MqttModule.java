package com.gamecard.dagger.module;

import android.app.Application;

import com.gamecard.view.HomeView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;

/**
 * Created by bridgeit on 9/8/16.
 */
@Module
public class MqttModule {

    private static final String mqttServerPath = "tcp://192.168.0.128:1883";
    private static final String CLIENT = MqttClient.generateClientId();


    @Provides
    @Singleton
    MqttAndroidClient provideMqttAndroidClient(Application application){
        return new MqttAndroidClient(application, mqttServerPath, CLIENT);
    }

}

package com.gamecard.utility;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.gamecard.dagger.component.DaggerMqttComponent;
import com.gamecard.dagger.component.DaggerNetComponent;
import com.gamecard.dagger.component.MqttComponent;
import com.gamecard.dagger.component.NetComponent;
import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.MqttModule;
import com.gamecard.dagger.module.NetModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bridgeit on 23/6/16.
 */

public class AppController extends Application {

    private NetComponent mNetComponent, netComponent1;

    private MqttComponent mqttComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
            //    .netModule(new NetModule("http://gamecarddemo.herokuapp.com/"))

                .netModule(new NetModule("http://192.168.0.142:8080/GameCard/"))
                .build();

        netComponent1 = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule("http://192.168.0.132:8080/GameCard/"))
                .build();

        mqttComponent= DaggerMqttComponent.builder()
                .appModule(new AppModule(this))
                .mqttModule(new MqttModule())
                .build();
    }

    public NetComponent getmNetComponent() {
        return mNetComponent;
    }

    public NetComponent getnetComponent1() {
        return netComponent1;
    }

    public MqttComponent getMqttComponent() {
        return mqttComponent;
    }



}

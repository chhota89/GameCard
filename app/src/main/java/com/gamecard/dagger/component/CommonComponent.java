package com.gamecard.dagger.component;

import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.CommonModule;
import com.gamecard.dagger.module.MqttModule;
import com.gamecard.view.BluetoothPeerList;
import com.gamecard.view.HomeView;
import com.gamecard.view.WiFiPeerList;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bridgeit on 7/10/16.
 */

@Singleton
@Component(modules = {AppModule.class,CommonModule.class})

public interface CommonComponent {
    void inject(WiFiPeerList wiFiPeerList);
    void inject(BluetoothPeerList bluetoothPeerList);
}

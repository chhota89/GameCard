package com.gamecard.callback;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by bridgeit on 30/6/16.
 */

public interface CallBackWifiBroadcast {
    void setIsWifiP2pEnabled(boolean flag);

    void resetData();

    void setNetworkToReadyState(boolean b, WifiP2pInfo wifiInfo, WifiP2pDevice device);

    void updateDevice(WifiP2pDevice device);
}

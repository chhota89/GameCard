package com.gamecard.utility;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.gamecard.callback.CallBackWifiBroadcast;
import com.gamecard.view.HomeView;
import com.gamecard.view.PeerList;

/**
 * Created by bridgeit on 28/6/16.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    Activity activity;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private CallBackWifiBroadcast callBackWifiBroadcast;

    /**
     * @param manager  WifiP2pManager system service
     * @param channel  Wifi p2p channel
     * @param activity callBackWifiBroadcast associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       CallBackWifiBroadcast callback, Activity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.callBackWifiBroadcast = callback;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate callBackWifiBroadcast

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                callBackWifiBroadcast.setIsWifiP2pEnabled(true);
            } else {
                callBackWifiBroadcast.setIsWifiP2pEnabled(false);
                callBackWifiBroadcast.resetData();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (manager != null) {
                if (activity instanceof PeerList)
                    manager.requestPeers(channel, (PeerList) activity);
                if (activity instanceof HomeView)
                    manager.requestPeers(channel, (HomeView) activity);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            if (networkState.isConnected()) {
                //set client state so that all needed fields to make a transfer are ready
                //callBackWifiBroadcast.setTransferStatus(true);
                callBackWifiBroadcast.setNetworkToReadyState(true, wifiInfo, device);
            } else {
                //set variables to disable file transfer and reset client back to original state
                //callBackWifiBroadcast.setTransferStatus(false);
                manager.cancelConnect(channel, null);

            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            callBackWifiBroadcast.updateDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}

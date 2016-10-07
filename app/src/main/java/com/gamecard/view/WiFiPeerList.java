package com.gamecard.view;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gamecard.R;
import com.gamecard.adapter.AdapterPeerList;
import com.gamecard.callback.CallBackWifiBroadcast;
import com.gamecard.callback.ClickListener;
import com.gamecard.utility.AppController;
import com.gamecard.utility.Constant;
import com.gamecard.utility.FileSendService;
import com.gamecard.utility.WiFiFileReceiver;
import com.gamecard.utility.RecyclerTouchListner;
import com.gamecard.utility.WiFiDirectBroadcastReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class WiFiPeerList extends AppCompatActivity implements WifiP2pManager.PeerListListener, CallBackWifiBroadcast {

    private static final String TAG = "WiFiPeerList";
    private static final int PORT = 8888;
    private final IntentFilter intentFilter = new IntentFilter();
    RecyclerView recyclerView;
    List<WifiP2pDevice> deviceList;
    AdapterPeerList adapterPeerList;
    WifiP2pInfo wifiP2pInfo;
    WifiP2pDevice device;
    Button send;
    boolean wifiIsOn=false;
    ProgressDialog mProgressDialog;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pManager manager;
  //  private ApplicationInfo applicationInfo;
    ProgressDialog progressDialog;
    CoordinatorLayout coordinatorLayout;
    String sourceDir, loadLabel;

    @Inject
    NotificationCompat.Builder mBuilder;

    @Inject
    NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppController)getApplication()).getCommonComponent().inject(this);

        setContentView(R.layout.activity_peer_list);

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout) ;

      //  applicationInfo = getIntent().getParcelableExtra("APPLICATION");
        sourceDir = getIntent().getStringExtra(VideoFragment.SOURCE_DIR);
        loadLabel = getIntent().getStringExtra(VideoFragment.LABEL_NAME);


        mBuilder.setContentTitle("Sending game "+loadLabel);

        progressDialog = new ProgressDialog(WiFiPeerList.this);
        progressDialog.setMessage("Searching ...");
        progressDialog.setCancelable(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        send = (Button) findViewById(R.id.send);

        //Setting the recycle view
        deviceList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.peerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterPeerList = new AdapterPeerList(WiFiPeerList.this, deviceList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterPeerList);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = deviceList.get(position).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        device = deviceList.get(position);
                        // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                        Toast.makeText(WiFiPeerList.this, "Request send successfully.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(WiFiPeerList.this, "Connect failed. Retry.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        manager.removeGroup(channel,null);
        searchPerer();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, this);
        registerReceiver(receiver, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    //Showing wifi message
    public void setIsWifiP2pEnabled(boolean status) {
        if (!status){
            wifiIsOn=false;
            turnOnWiFi();
        } else {
            wifiIsOn=true;
        }
    }

    private void turnOnWiFi() {
        Snackbar.make(coordinatorLayout, getString(R.string.turn_on_wifi), Snackbar.LENGTH_LONG)
                .setAction("turn on", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).show();
    }

    //starting the services
    public void updateDevice(WifiP2pDevice device) {

        /*if(device.status==WifiP2pDevice.CONNECTED){
            if(device.isGroupOwner()){
                Toast.makeText(WiFiPeerList.this,"Is connected and Group owner",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(WiFiPeerList.this,"Is connected NOT Group owner",Toast.LENGTH_LONG).show();
            }
        }*/
    }


    //Reset Data
    public void resetData() {
        deviceList.clear();
        adapterPeerList.notifyDataSetChanged();
    }

    //This method is called when peer is available
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        progressDialog.hide();
        deviceList.clear();
        deviceList.addAll(peers.getDeviceList());
        adapterPeerList.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_peer_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                searchPerer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchPerer() {

            //start searching for peers
            progressDialog.show();
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(WiFiPeerList.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(WiFiPeerList.this, "Discovery Failed : " + reasonCode,
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void sendFile(View view) {

        final File appsDir = new File(sourceDir);

        if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
            Intent fileSendService = new Intent(this, FileSendService.class);
            fileSendService.putExtra(Constant.FILE_TO_SEND, appsDir);
            fileSendService.putExtra(Constant.PORT, new Integer(PORT));
            fileSendService.putExtra(Constant.WI_FI_INFO, wifiP2pInfo);
            fileSendService.putExtra(Constant.WIFI_P2P_DEVICE, device);
            fileSendService.putExtra(Constant.RESULT_RECIVER, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, final Bundle resultData) {
                    if (resultCode == PORT) {
                        if (mProgressDialog != null) {

                            int percentage = resultData.getInt(Constant.PROGRESS, 0);
                            mProgressDialog.setProgress(percentage);
                            mBuilder.setProgress(100, percentage, false);
                            mBuilder.setContentText("progress ... "+percentage+" %");
                            if (percentage == 100) {
                                mProgressDialog.hide();
                                mProgressDialog=null;
                                mBuilder.setContentText("Sending Finish.").setProgress(0,0,false);

                            }
                        }
                        else{
                            //Initialize the progress dialog
                            mProgressDialog = new ProgressDialog(WiFiPeerList.this);
                            mProgressDialog.setMessage(getResources().getString(R.string.sending)+loadLabel);
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.setMax(100);
                            mProgressDialog.setProgressNumberFormat(null);
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.show();
                        }
                        mNotificationManager.notify(Constant.SEND_WIFI_NOTIFICATION, mBuilder.build());
                    }
                }
            });
            startService(fileSendService);
        } else {
            Toast.makeText(WiFiPeerList.this, "First Connect the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void setNetworkToReadyState(boolean b, WifiP2pInfo wifiInfo, WifiP2pDevice device) {

        //when group is formed create one thread that is wait for connection to avilable
        this.wifiP2pInfo = wifiInfo;
        this.device = device;
        if (wifiInfo.groupFormed) {
            if (!wifiInfo.isGroupOwner) {
                send.setVisibility(View.VISIBLE);
            } else
                send.setVisibility(View.INVISIBLE);
        }
    }
}

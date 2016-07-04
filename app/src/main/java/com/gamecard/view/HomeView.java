package com.gamecard.view;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.gamecard.R;
import com.gamecard.adapter.AdapterDisplayApp;
import com.gamecard.callback.CallBackString;
import com.gamecard.callback.CallBackWifiBroadcast;
import com.gamecard.callback.ClickListener;
import com.gamecard.utility.ApplicationUtility;
import com.gamecard.utility.CircleTransformation;
import com.gamecard.utility.EditSharedPrefrence;
import com.gamecard.utility.FacebookUtility;
import com.gamecard.utility.FileServerAsyncTask;
import com.gamecard.utility.RecyclerTouchListner;
import com.gamecard.utility.WiFiDirectBroadcastReceiver;
import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.picasso.Picasso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class HomeView extends AppCompatActivity implements CallBackWifiBroadcast, WifiP2pManager.PeerListListener {

    private static final String TAG = "HomeView";
    private static final int GALLERY_CODE = 214;
    private final String FACEBOOK_URL = "https://graph.facebook.com/";
    private final IntentFilter intentFilter = new IntentFilter();
    SwitchButton switchButton;
    TextView reciveFile;
    CoordinatorLayout coordinatorLayout;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pManager manager;
    boolean wifiOn=false,facebookLogin=false;
    TextView userName;
    ImageView profilePic;
    ImageView imageView;
    Uri selectedPic=null;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        //collapsingToolbarLayout.setBackground(getResources().getDrawable(R.drawable.background));

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        userName = (TextView) findViewById(R.id.userName);
        imageView = (ImageView) findViewById(R.id.profilePic);
        facebookLogin=getIntent().getBooleanExtra("Facebook_Login",false);

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo info : services) {
            //cal.setTimeInMillis(currentMillis-info.activeSince);
            long timing=System.currentTimeMillis()-info.activeSince;
            Log.i(TAG, "onCreate: "+info.process+"    "+timing);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.empty);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        reciveFile = (TextView) findViewById(R.id.reciveFile);
        switchButton = (SwitchButton) findViewById(R.id.switchButton);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(wifiOn){
                        reciveFile.setText("Receiver started");
                        Snackbar.make(coordinatorLayout, "Reciver started.", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        switchButton.setChecked(false);
                                    }
                                }).show();

                        manager.createGroup(channel,null);
                        //manager.discoverPeers(channel, null);
                    }
                    else{
                        switchButton.setChecked(false);
                        Snackbar.make(coordinatorLayout, "Turn on P2P setting.", Snackbar.LENGTH_LONG)
                                .setAction("turn on", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                }).show();
                    }
                } else {
                    reciveFile.setText(getString(R.string.reciveFile));
                    manager.removeGroup(channel,null);
                }
            }
        });

        //Hiding the toolbar text when collapsing is expanded
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Title");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(getString(R.string.empty));
                    isShow = false;
                }
            }
        });


        if(facebookLogin){
            //Loading Profile picture
            String userId = AccessToken.getCurrentAccessToken().getUserId();
            Picasso.with(HomeView.this).load(FACEBOOK_URL + userId + "/picture?type=large").transform(new CircleTransformation()).into(imageView);

            //Setting user Name
            FacebookUtility.getUserName(new CallBackString() {
                @Override
                public void onSuccess(String name) {
                    EditSharedPrefrence.setUserName(name,HomeView.this);
                    userName.setText(name);
                    chageWiFiDirectUserName(name);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "onError: " + error);
                }

            });
        }else{
            String nameOfUser=EditSharedPrefrence.getUserName(getApplicationContext());
            if(nameOfUser!=null){
                userName.setText(nameOfUser);
                chageWiFiDirectUserName(nameOfUser);
            }
            String profileUri=EditSharedPrefrence.getProfilePic(getApplicationContext());
            if(profileUri!=null)
                Picasso.with(HomeView.this).load(profileUri).transform(new CircleTransformation()).into(imageView);
        }

        final List<Object> installAppList = ApplicationUtility.getInstallApp(ApplicationUtility.getAllPackages(HomeView.this));
        installAppList.add(0, "Discover new games");
        installAppList.add(4, "Installed games ");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.appRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        AdapterDisplayApp adapterDisplayApp = new AdapterDisplayApp(installAppList, HomeView.this);
        recyclerView.setAdapter(adapterDisplayApp);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (installAppList.get(position) instanceof ApplicationInfo) {
                    Intent intent = new Intent(HomeView.this, AppDetailsActivity.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        ImageView imageView1 = (ImageView) view.findViewById(R.id.appLogo);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(HomeView.this, imageView1, imageView1.getTransitionName());
                        intent.putExtra("APPLICATION", (ApplicationInfo) installAppList.get(position));
                        startActivity(intent, options.toBundle());
                    } else {
                        intent.putExtra("APPLICATION", (ApplicationInfo) installAppList.get(position));
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ...........................");
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, this);
        registerReceiver(receiver, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                if(facebookLogin){
                    EditSharedPrefrence.setUserLogin(false,getApplicationContext());
                    //Logout from Facebook.
                    LoginManager.getInstance().logOut();
                    //Redirecting to Login Screen
                    startActivity(new Intent(HomeView.this, LogInActivity.class));
                }
                return true;

            case R.id.setUserName:
                settingDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setIsWifiP2pEnabled(boolean flag) {
        wifiOn=flag;
    }

    @Override
    public void resetData() {

    }

    @Override
    public void setNetworkToReadyState(boolean b, WifiP2pInfo wifiInfo, WifiP2pDevice device) {
        if (wifiInfo.groupFormed) {
            FileServerAsyncTask fileServerAsyncTask = new FileServerAsyncTask(HomeView.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fileServerAsyncTask.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
            } else
                fileServerAsyncTask.execute();
        }
    }

    @Override
    public void updateDevice(WifiP2pDevice device) {
        if(device.status == WifiP2pDevice.CONNECTED){
            reciveFile.setText("Connected ...");
            switchButton.setChecked(true);
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
    }

    private void settingDialog(){
        final Dialog dialog = new Dialog(HomeView.this);
        dialog.setContentView(R.layout.setting_dialog);
        dialog.setTitle("Update Profile");

        Button update=(Button)dialog.findViewById(R.id.update);
        final EditText userName1=(EditText)dialog.findViewById(R.id.userName);

        profilePic =(ImageView)dialog.findViewById(R.id.profilePic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, GALLERY_CODE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=userName1.getText().toString();
                if(!name.equals("")){
                    EditSharedPrefrence.setUserName(name,getApplicationContext());
                    chageWiFiDirectUserName(name);
                    userName.setText(name);
                }
                if(selectedPic!=null){
                    EditSharedPrefrence.setProfilePic(selectedPic.toString(),getApplicationContext());
                    Picasso.with(HomeView.this).load(selectedPic).transform(new CircleTransformation()).into(imageView);
                }
                dialog.hide();
            }
        });

        dialog.show();
    }

    private void chageWiFiDirectUserName(String name){
        try {
            Method m = manager.getClass().getMethod("setDeviceName", new Class[]{channel.getClass(), String.class,
                    WifiP2pManager.ActionListener.class});

            try {
                m.invoke(manager, channel, name, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onSuccess: Device name change successfully");
                    }
                    @Override
                    public void onFailure(int reason) {
                        Log.i(TAG, "OnError: Device name change failed REASON"+reason);
                    }
                });
            } catch (IllegalAccessException e) {
                Log.e(TAG, "On Device Chane name: ",e );
            } catch (InvocationTargetException e) {
                Log.e(TAG, "On Device Chane name: ",e );
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "On Device Chane name: ",e );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode==GALLERY_CODE){
                selectedPic=data.getData();
                Picasso.with(HomeView.this).load(data.getData()).transform(new CircleTransformation()).into(profilePic);
            }
        }
    }
}

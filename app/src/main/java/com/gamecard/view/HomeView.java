package com.gamecard.view;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.gamecard.R;
import com.gamecard.adapter.AdapterDisplayApp;
import com.gamecard.callback.CallBackBluetooth;
import com.gamecard.callback.CallBackMqtt;
import com.gamecard.callback.CallBackString;
import com.gamecard.callback.CallBackWifiBroadcast;
import com.gamecard.callback.ClickListener;
import com.gamecard.controller.MqttController;
import com.gamecard.controller.RestCall;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.PackageModel;
import com.gamecard.utility.ApplicationUtility;
import com.gamecard.utility.BluethoothFileReciver;
import com.gamecard.utility.BluetoothBroadcastReciver;
import com.gamecard.utility.CircleTransformation;
import com.gamecard.utility.EditSharedPrefrence;
import com.gamecard.utility.FacebookUtility;
import com.gamecard.utility.RecyclerTouchListner;
import com.gamecard.utility.WiFiDirectBroadcastReceiver;
import com.gamecard.utility.WiFiFileReceiver;
import com.kyleduo.switchbutton.SwitchButton;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class HomeView extends AppCompatActivity implements CallBackWifiBroadcast, WifiP2pManager.PeerListListener, CallBackBluetooth {

    private static final String TAG = "HomeView";
    private static final String mqttServerPath = "tcp://192.168.0.150:1883";
    private static final int GALLERY_CODE = 214;
    private static final int REQUEST_ENABLE_BT = 215;
    private static final int BLUETOOTH_DISCOVRABLE = 216;
    private static final int ASK_READ_WRITE_PERMISSION = 219;
    private static final int WIFI_READ_WRITE_PERMISSION = 220;
    private static final int PIC_CROP = 221;
    private static final int TURN_ON_WIFI = 222;
    private static final int TAKE_READ_WRITE_PROFILE_STORAGE = 223;
    private static final String CLIENT = MqttClient.generateClientId();
    private final String FACEBOOK_URL = "https://graph.facebook.com/";
    private final IntentFilter intentFilter = new IntentFilter();
    IntentFilter bluetoothIntent = new IntentFilter();
    SwitchButton wifiSwitch, bluetoothSwitch;
    TextView reciveFile;
    CoordinatorLayout coordinatorLayout;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pManager manager;
    boolean wifiOn = false, facebookLogin = false;
    TextView userName;
    ImageView profilePic;
    ImageView imageView;
    Uri selectedPic = null;
    Uri outPutUri = null;
    BluetoothAdapter mBluetoothAdapter;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private BluetoothBroadcastReciver bluetoothBroadcastReciver;
    private Realm realm;
    List<Object> gameList;
    AdapterDisplayApp adapterDisplayApp;
    Map<String, ApplicationInfo> applicationInfoMap;
    List<String> packageList, dbList;
    RealmResults<GameResponseModel> realmResults;
    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        //collapsingToolbarLayout.setBackground(getResources().getDrawable(R.drawable.background));
        realm = Realm.getInstance(HomeView.this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        bluetoothIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        userName = (TextView) findViewById(R.id.userName);
        imageView = (ImageView) findViewById(R.id.profilePic);
        facebookLogin = getIntent().getBooleanExtra("Facebook_Login", false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog();
            }
        });

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo info : services) {
            //cal.setTimeInMillis(currentMillis-info.activeSince);
            long timing = System.currentTimeMillis() - info.activeSince;
            Log.i(TAG, "onCreate: " + info.process + "    " + timing);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.empty);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        //setting background for collapsing toolbar
        /*Picasso.with(HomeView.this).load(R.drawable.background).into(new Target(){

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });*/


        toolbarTextAppernce();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        reciveFile = (TextView) findViewById(R.id.reciveFile);
        bluetoothSwitch = (SwitchButton) findViewById(R.id.bluetooth);
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mBluetoothAdapter == null) {
                        // Device does not support Bluetooth
                        Snackbar.make(coordinatorLayout, "This device is not support Bluethooth", Snackbar.LENGTH_LONG).show();
                    } else {
                        //Device has bluetooth
                        if (!mBluetoothAdapter.isEnabled()) {
                            //bluetoothSwitch.setChecked(false);

                            //request to turn on bluetooth
                            turnOnBluetoothIntent();
                        } else if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                            //bluetoothSwitch.setChecked(false);

                            //Request to make device discoverable.
                            requestToMakeBluetoothDiscovrable();
                        } else {
                            //Start the Receiver thread.
                            if (Build.VERSION.SDK_INT >= 23)
                                takeRunTimePermissionForStorage(ASK_READ_WRITE_PERMISSION);
                            else
                                startBluetoothReciver();

                        }
                    }
                }
            }
        });

        wifiSwitch = (SwitchButton) findViewById(R.id.switchButton);
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (wifiOn) {
                        reciveFile.setText("Receiver started");
                        Snackbar.make(coordinatorLayout, "Receiver started.", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        wifiSwitch.setChecked(false);
                                    }
                                }).show();

                        manager.createGroup(channel, null);
                        //manager.discoverPeers(channel, null);
                    } else {
                        wifiSwitchIsOff();
                    }
                } else {
                    reciveFile.setText(getString(R.string.reciveFile));
                    manager.removeGroup(channel, null);
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


        if (facebookLogin) {
            //Loading Profile picture
            String userId = AccessToken.getCurrentAccessToken().getUserId();
            Picasso.with(HomeView.this).load(FACEBOOK_URL + userId + "/picture?type=large").transform(new CircleTransformation()).into(imageView);

            //Setting user Name
            FacebookUtility.getUserName(new CallBackString() {
                @Override
                public void onSuccess(String name) {
                    EditSharedPrefrence.setUserName(name, HomeView.this);
                    userName.setText(name);
                    chageWiFiDirectUserName(name);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "onError: " + error);
                }

            });
        } else {
            String nameOfUser = EditSharedPrefrence.getUserName(getApplicationContext());
            if (nameOfUser != null) {
                userName.setText(nameOfUser);
                chageWiFiDirectUserName(nameOfUser);
            }
            String profileUri = EditSharedPrefrence.getProfilePic(getApplicationContext());
            if (profileUri != null)
                Picasso.with(HomeView.this).load(profileUri).transform(new CircleTransformation()).into(imageView);
        }

        gameList = new ArrayList<>();
        adapterDisplayApp = new AdapterDisplayApp(gameList, HomeView.this);
        applicationInfoMap = ApplicationUtility.getInstallApp(ApplicationUtility.getAllPackages(HomeView.this));
        packageList = new ArrayList<String>(applicationInfoMap.keySet());
        dbList = new ArrayList<>();
        gameList.add(0, "Discover new games");

        // Build the query looking at all users:
        //Note: make this as async
        realmResults = realm.where(GameResponseModel.class).findAll();
        displayList();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.appRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(defaultItemAnimator);
        recyclerView.setAdapter(adapterDisplayApp);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (gameList.get(position) instanceof ApplicationInfo) {
                    Intent intent = new Intent(HomeView.this, AppDetailsActivity.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        ImageView imageView1 = (ImageView) view.findViewById(R.id.appLogo);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(HomeView.this, imageView1, imageView1.getTransitionName());
                        intent.putExtra("APPLICATION", (ApplicationInfo) gameList.get(position));
                        startActivity(intent, options.toBundle());
                    } else {
                        intent.putExtra("APPLICATION", (ApplicationInfo) gameList.get(position));
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void displayList() {

        //display game in the grid view
        for (GameResponseModel gameResponseModel : realmResults) {
            if (gameResponseModel.getIsgame()) {
                if (applicationInfoMap.containsKey(gameResponseModel.getPackagename()))
                    gameList.add(applicationInfoMap.get(gameResponseModel.getPackagename()));
            }
            dbList.add(gameResponseModel.getPackagename());
        }

        //Find the difference between database results  and install application
        packageList.removeAll(dbList);

        if (packageList.size() != 0)
            loadDataFromRest();

    }

    private void loadDataFromRest() {

        final PackageModel packageModel = new PackageModel(packageList);
        packageModel.setTopic(CLIENT);
        client = new MqttAndroidClient(HomeView.this, mqttServerPath, CLIENT);
        final MqttController mqttController = new MqttController(CLIENT,client);
        //establish connection with mqtt server
        mqttController.connectToMqtt(new MqttController.CallBackConnectMqtt() {
            @Override
            public void onConnectionSuccess() {

                //Subscribe to mqtt topic
                mqttController.subcibeTopic(realm, new CallBackMqtt() {
                    @Override
                    public void onMessageRecive(GameResponseModel gameResponseModel) {
                        //response receive from mqtt server
                        if (gameResponseModel.getIsgame() && applicationInfoMap.containsKey(gameResponseModel.getPackagename())) {
                            gameList.add(applicationInfoMap.get(gameResponseModel.getPackagename()));
                            adapterDisplayApp.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(HomeView.this, "Exception " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                //make rest call to find the game from package List
                RestCall.sendPackageList(packageModel);
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    private void takeRunTimePermissionForStorage(int work) {
        if (Build.VERSION.SDK_INT >= 23) {
            //check for ACCESS_COARSE_LOCATION permission
            int hasPermission = ActivityCompat.checkSelfPermission(HomeView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ActivityCompat.checkSelfPermission(HomeView.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (work == ASK_READ_WRITE_PERMISSION && hasPermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
                startBluetoothReciver();
                return;
            } else if (work == WIFI_READ_WRITE_PERMISSION && hasPermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
                WiFiFileReceiver wiFiFileReceiver = new WiFiFileReceiver(HomeView.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    wiFiFileReceiver.executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
                } else
                    wiFiFileReceiver.execute();
                return;
            } else if (work == WIFI_READ_WRITE_PERMISSION && hasPermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
                outPutUri = Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()));
                return;
            }


            ActivityCompat.requestPermissions(HomeView.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    ASK_READ_WRITE_PERMISSION);
        }
    }

    private void turnOnBluetoothIntent() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        /*bluetoothBroadcastReciver =new BluetoothBroadcastReciver(this);
        registerReceiver(bluetoothBroadcastReciver, bluetoothIntent);*/
    }

    private void requestToMakeBluetoothDiscovrable() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 400);
        startActivityForResult(intent, BLUETOOTH_DISCOVRABLE);
    }

    private void wifiSwitchIsOff() {
        wifiSwitch.setChecked(false);
        Snackbar.make(coordinatorLayout, getString(R.string.turn_on_wifi), Snackbar.LENGTH_LONG)
                .setAction("turn on", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), TURN_ON_WIFI);
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, this);
        registerReceiver(receiver, intentFilter);

        bluetoothBroadcastReciver = new BluetoothBroadcastReciver(this);
        registerReceiver(bluetoothBroadcastReciver, bluetoothIntent);

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(bluetoothBroadcastReciver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client!=null)
            client.unregisterResources();

        realm.close();
    }

    private void startBluetoothReciver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new BluethoothFileReciver(HomeView.this, mBluetoothAdapter).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
        } else
            new BluethoothFileReciver(HomeView.this, mBluetoothAdapter).execute();
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
                if (facebookLogin) {
                    EditSharedPrefrence.setUserLogin(false, getApplicationContext());
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
        wifiOn = flag;
        if (!flag)
            wifiSwitchIsOff();
    }

    @Override
    public void resetData() {

    }

    @Override
    public void setNetworkToReadyState(boolean b, WifiP2pInfo wifiInfo, WifiP2pDevice device) {
        if (wifiInfo.groupFormed) {

            //take permission and start reciver thread based on it
            /*if(Build.VERSION.SDK_INT>=23)
                takeRunTimePermissionForStorage(WIFI_READ_WRITE_PERMISSION);
            else{*/
            WiFiFileReceiver wiFiFileReceiver = new WiFiFileReceiver(HomeView.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                wiFiFileReceiver.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
            } else
                wiFiFileReceiver.execute();
            //}

        }
    }

    @Override
    public void updateDevice(WifiP2pDevice device) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
    }

    private void settingDialog() {
        final Dialog dialog = new Dialog(HomeView.this);
        dialog.setContentView(R.layout.setting_dialog);
        dialog.setTitle("Update Profile");

        Button update = (Button) dialog.findViewById(R.id.update);
        final EditText userName1 = (EditText) dialog.findViewById(R.id.userName);

        profilePic = (ImageView) dialog.findViewById(R.id.profilePic);
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
                String name = userName1.getText().toString();
                if (!name.equals("")) {
                    EditSharedPrefrence.setUserName(name, getApplicationContext());
                    chageWiFiDirectUserName(name);
                    userName.setText(name);
                }
                if (outPutUri != null) {
                    EditSharedPrefrence.setProfilePic(outPutUri.toString(), getApplicationContext());
                    Picasso.with(HomeView.this).load(outPutUri).transform(new CircleTransformation()).into(imageView);
                }
                dialog.hide();
            }
        });

        dialog.show();
    }

    private void chageWiFiDirectUserName(String name) {
        //manager.setDeviceName(manager, channel, name, null);
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
                        Log.i(TAG, "OnError: Device name change failed REASON" + reason);
                    }
                });
            } catch (IllegalAccessException e) {
                Log.e(TAG, "On Device Chane name: ", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "On Device Chane name: ", e);
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "On Device Chane name: ", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                selectedPic = data.getData();
                /*if(Build.VERSION.SDK_INT>=23)
                    takeRunTimePermissionForStorage(TAKE_READ_WRITE_PROFILE_STORAGE);
                else*/
                outPutUri = Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()));

                //cropImage();
                Crop.of(selectedPic, outPutUri).asSquare().start(HomeView.this);
                Picasso.with(HomeView.this).load(data.getData()).transform(new CircleTransformation()).into(profilePic);
            } else if (requestCode == Crop.REQUEST_CROP) {
                outPutUri = Crop.getOutput(data);
                Picasso.with(HomeView.this).load(outPutUri).transform(new CircleTransformation()).into(profilePic);
            } else if (requestCode == REQUEST_ENABLE_BT) {
                //check for bluetooth is discoverable or not
                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                    requestToMakeBluetoothDiscovrable();
            }
        }
        if (requestCode == BLUETOOTH_DISCOVRABLE && resultCode == 400) {
            //Start the Receiver thread.
            if (Build.VERSION.SDK_INT >= 23)
                takeRunTimePermissionForStorage(ASK_READ_WRITE_PERMISSION);
            else
                startBluetoothReciver();

            bluetoothSwitch.setChecked(true);
        }
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == REQUEST_ENABLE_BT || requestCode == BLUETOOTH_DISCOVRABLE)
                bluetoothSwitch.setChecked(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ASK_READ_WRITE_PERMISSION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startBluetoothReciver();
                }
            case WIFI_READ_WRITE_PERMISSION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    WiFiFileReceiver wiFiFileReceiver = new WiFiFileReceiver(HomeView.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        wiFiFileReceiver.executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
                    } else
                        wiFiFileReceiver.execute();
                }
            case TAKE_READ_WRITE_PROFILE_STORAGE:
                outPutUri = Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()));
                break;

        }
    }

    @Override
    public void addDevice(BluetoothDevice device) {

    }

    @Override
    public void deviceState(boolean flag) {
        if (bluetoothSwitch.isChecked() && !flag) {
            bluetoothSwitch.setChecked(false);
            Snackbar.make(coordinatorLayout, "Turn on Bluetooth", Snackbar.LENGTH_LONG).setAction("TurnOn", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnOnBluetoothIntent();

                }
            }).show();
        }
    }
}

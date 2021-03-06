package com.gamecard.view;

import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
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
import com.gamecard.utility.AppController;
import com.gamecard.utility.BluethoothFileReciver;
import com.gamecard.utility.BluetoothBroadcastReceiver;
import com.gamecard.utility.CircleTransformation;
import com.gamecard.utility.Constant;
import com.gamecard.utility.EditSharedPrefrence;
import com.gamecard.utility.FacebookUtility;
import com.gamecard.utility.FileUtility;
import com.gamecard.utility.LoadDbAsync;
import com.gamecard.utility.RecyclerTouchListner;
import com.gamecard.utility.WiFiDirectBroadcastReceiver;
import com.gamecard.utility.WiFiFileReceiver;
import com.kyleduo.switchbutton.SwitchButton;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class HomeView extends AppCompatActivity implements CallBackWifiBroadcast, WifiP2pManager.PeerListListener, CallBackBluetooth {

    @Inject
    MqttAndroidClient client;

    private static final String TAG = "HomeView";
    private static final int GALLERY_CODE = 214;
    private static final int REQUEST_ENABLE_BT = 215;
    private static final int BLUETOOTH_DISCOVERABLE = 216;
    private static final int BLUETOOTH_READ_WRITE_PERMISSION = 219;
    private static final int WIFI_READ_WRITE_PERMISSION = 220;
    private static final int PIC_CROP = 221;
    private static final int TURN_ON_WIFI = 222;
    private static final int TAKE_READ_WRITE_PROFILE_STORAGE = 223;
    public static final String NEW_PACKAGE_LIST="NEW_PACKAGE_LIST";
    public static final String GAME_LIST="GAME_LIST";
    public static final String APPLICATION_INFO_MAP="APPLICATION_INFO_MAP";
    public static final String SUGGESTION_LIST="SUGGESTION_LIST";
    private final String FACEBOOK_URL = "https://graph.facebook.com/";
    public static final String SUGGESTION="SUGGESTION";
    private final IntentFilter intentFilter = new IntentFilter();
    IntentFilter bluetoothIntent = new IntentFilter();
    SwitchButton wifiSwitch, bluetoothSwitch;
    TextView receiveFile;
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
    AppBarLayout appBarLayout;
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private Realm realm;
    List<Object> gameList;
    AdapterDisplayApp adapterDisplayApp;
    Map<String, ApplicationInfo> applicationInfoMap;
    List<String> packageList;
    RealmResults<GameResponseModel> suggestion;
    RealmChangeListener realmChangeListener;
    RecyclerView recyclerView;
    public static boolean bluetoothAsyncTaskStarted=false,wiFiAsyncTaskStarted=false;

    boolean suggestionFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_home_view);

        //Initialize all view
        initView();

        //Register broadcast receiver and Initialize relam
        initComponent();

        //Set listener for view
        setListnerForView();

        //set Profile picture
        setProfilePicture();

        gameList = new ArrayList<>();
        gameList.add(0, getString(R.string.continue_playing));
        adapterDisplayApp = new AdapterDisplayApp(gameList, HomeView.this);

        // Load data from local database.
        LoadDbAsync loadDbAsync=new LoadDbAsync(HomeView.this){
            @Override
            protected void onPostExecute(Map<String, Object> responseMap) {
                super.onPostExecute(responseMap);
                List<ApplicationInfo> gameLisDisplay= (List<ApplicationInfo>) responseMap.get(HomeView.GAME_LIST);
                gameList.addAll(gameLisDisplay);

                suggestion=getSuggestionList();
                if(suggestion.size()!=0) {
                    gameList.add(getString(R.string.suggestion));
                    gameList.addAll(suggestion);
                }
                /*realmChangeListener=new RealmChangeListener() {
                    boolean suggestionFlag=false;
                    @Override
                    public void onChange() {
                        if(suggestion!=null && suggestion.size()!=0){
                            if(!suggestionFlag)
                                gameList.add("Suggestion");
                            suggestionFlag=true;
                            gameList.addAll(suggestion);
                            adapterDisplayApp.notifyDataSetChanged();
                        }
                    }
                };
                suggestion.addChangeListener(realmChangeListener);*/

                adapterDisplayApp.notifyDataSetChanged();
                applicationInfoMap= (Map<String, ApplicationInfo>) responseMap.get(HomeView.APPLICATION_INFO_MAP);
                //check for new application is install or not
                packageList=(List<String>) responseMap.get(HomeView.NEW_PACKAGE_LIST);
                if(packageList.size()!=0) {
                    loadDataFromRest();
                }

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            loadDbAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            loadDbAsync.execute();
        }

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
                Intent intent = new Intent(HomeView.this, AppDescriptionActivity.class);
                if (gameList.get(position) instanceof ApplicationInfo) {
                    intent.putExtra(Constant.APPLICATION,  ((ApplicationInfo) gameList.get(position)).packageName);
                    intent.putExtra(VideoFragment.LABEL_NAME,((ApplicationInfo) gameList.get(position)).loadLabel(getPackageManager()));
                    intent.putExtra(VideoFragment.SOURCE_DIR,((ApplicationInfo) gameList.get(position)).sourceDir);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ImageView imageView1 = (ImageView) view.findViewById(R.id.appLogo);
                        /*ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(HomeView.this, imageView1, imageView1.getTransitionName());*/
                        startActivity(intent/*, options.toBundle()*/);
                    } else {
                        startActivity(intent);
                    }
                }
                if(gameList.get(position) instanceof GameResponseModel){
                    intent.putExtra("APPLICATION",((GameResponseModel) gameList.get(position)).getPackagename());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void initView(){
        userName = (TextView) findViewById(R.id.userName);
        imageView = (ImageView) findViewById(R.id.profilePic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.empty);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        receiveFile = (TextView) findViewById(R.id.reciveFile);
        bluetoothSwitch = (SwitchButton) findViewById(R.id.bluetooth);
        wifiSwitch = (SwitchButton) findViewById(R.id.switchButton);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        recyclerView = (RecyclerView) findViewById(R.id.appRecycleView);

    }

    public RealmResults<GameResponseModel> getSuggestionList() {
        return realm.where(GameResponseModel.class).equalTo("suggestion", true).findAll();
    }

    private void setListnerForView(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog();
            }
        });


        //Hiding the toolbar text when collapsing is expanded
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

        toolbarTextAppernce();
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mBluetoothAdapter == null) {
                        // Device does not support Bluetooth
                        Snackbar.make(coordinatorLayout, getString(R.string.bluethooth_not_supported), Snackbar.LENGTH_LONG).show();
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
                            //check device free storage space
                            if(FileUtility.checkSpaceInSDcard()) {
                                //Start the Receiver thread.
                                if (Build.VERSION.SDK_INT >= 23)
                                    takeRunTimePermissionForStorage(BLUETOOTH_READ_WRITE_PERMISSION);
                                else
                                    startBluetoothReciver();
                            }else{
                                //Show alert dialog and redirect to file system
                                showNoSpaceAlert();
                                bluetoothSwitch.setChecked(false);
                            }

                        }
                    }
                }
            }
        });

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (wifiOn) {
                        //Check for SD card space
                        if (FileUtility.checkSpaceInSDcard()) {
                            receiveFile.setText(getString(R.string.reciver_started));
                            Snackbar.make(coordinatorLayout, "Receiver started.", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            wifiSwitch.setChecked(false);
                                        }
                                    }).show();

                            manager.createGroup(channel, null);

                            //Start the Receiver thread.
                                if (Build.VERSION.SDK_INT >= 23)
                                    takeRunTimePermissionForStorage(WIFI_READ_WRITE_PERMISSION);
                                else
                                    startWIfiReciver();

                            //manager.discoverPeers(channel, null);
                        }else{
                            //Show alert dialog and redirect to file system
                            showNoSpaceAlert();
                            wifiSwitch.setChecked(false);
                        }
                    } else {
                        wifiSwitchIsOff();
                    }
                } else {
                    receiveFile.setText(getString(R.string.reciveFile));
                    manager.removeGroup(channel, null);
                }
            }
        });
    }

    private void showNoSpaceAlert(){
        AlertDialog alertDialog=new AlertDialog.Builder(HomeView.this).create();
        alertDialog.setTitle(getString(R.string.no_space_avilable));
        alertDialog.setMessage(getString(R.string.make_some_space));
        alertDialog.setIcon(R.mipmap.ic_launcher_game_center);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.open)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setDataAndType(Uri.fromFile(Environment.getExternalStorageDirectory()), "*/*");
                        startActivity(intent);
                    }
                });
        alertDialog.show();
    }
    private void initComponent(){
        realm = Realm.getInstance(HomeView.this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        bluetoothIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setProfilePicture(){
        //setting background for collapsing toolbar
        Picasso.with(HomeView.this).load(R.drawable.background).into(new Target(){

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
        });


        facebookLogin = getIntent().getBooleanExtra("Facebook_Login", false);
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

    }

    private void loadDataFromRest() {

        final PackageModel packageModel = new PackageModel(packageList);

        final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        packageModel.setTopic(android_id);
        int  osVersion1 =  Build.VERSION.SDK_INT;
        String manufacturer1 = Build.MANUFACTURER;
        packageModel.setVersion(osVersion1);
        packageModel.setManufacturer(manufacturer1);
        ((AppController)getApplication()).getMqttComponent().inject(this);
        final MqttController mqttController = new MqttController(client);
        //establish connection with mqtt server
        mqttController.connectToMqtt(new MqttController.CallBackConnectMqtt() {
            @Override
            public void onConnectionSuccess() {

                //Subscribe to mqtt topic for installGame
                mqttController.subcibeTopic(realm,android_id,new CallBackMqtt() {
                    @Override
                    public void onMessageRecive(GameResponseModel gameResponseModel) {

                        //saveDataToRealm(gameResponseModel);
                        //response receive from mqtt server
                        if (gameResponseModel.getIsgame() && !gameResponseModel.getSuggestion() && applicationInfoMap.containsKey(gameResponseModel.getPackagename())) {
                            gameList.add(1,applicationInfoMap.get(gameResponseModel.getPackagename()));
                            adapterDisplayApp.notifyItemInserted(1);
                        }

                        else if(gameResponseModel.getSuggestion() && gameResponseModel.getIsgame()){
                            if(!suggestionFlag)
                                gameList.add("Suggestion");
                            suggestionFlag=true;
                            gameList.add(gameResponseModel);
                            adapterDisplayApp.notifyItemInserted(gameList.size()-1);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(HomeView.this, "Exception " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                //make rest call to find the game from package List
                new RestCall(HomeView.this).sendPackageList(packageModel);
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    //store data to realm
    private void saveDataToRealm(GameResponseModel gameResponseModel){
        realm.beginTransaction();
        realm.copyToRealm(gameResponseModel);
        realm.commitTransaction();
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

            if (work == BLUETOOTH_READ_WRITE_PERMISSION && hasPermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
                startBluetoothReciver();
                return;
            } else if (work == WIFI_READ_WRITE_PERMISSION && hasPermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
                startWIfiReciver();
                return;
            }

            ActivityCompat.requestPermissions(HomeView.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    work);
        }
    }

    private void turnOnBluetoothIntent() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        /*bluetoothBroadcastReceiver =new BluetoothBroadcastReceiver(this);
        registerReceiver(bluetoothBroadcastReceiver, bluetoothIntent);*/
    }

    private void requestToMakeBluetoothDiscovrable() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 400);
        startActivityForResult(intent, BLUETOOTH_DISCOVERABLE);
    }

    private void wifiSwitchIsOff() {
        wifiSwitch.setChecked(false);
        Snackbar.make(coordinatorLayout, getString(R.string.turn_on_wifi), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.turn_on), new View.OnClickListener() {
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

        bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(this);
        registerReceiver(bluetoothBroadcastReceiver, bluetoothIntent);

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(bluetoothBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client!=null)
            client.unregisterResources();
        suggestion.removeChangeListeners();
        realm.close();
    }

    private void startBluetoothReciver() {
        if(!bluetoothAsyncTaskStarted) {
            BluethoothFileReciver bluethoothFileReciver=new BluethoothFileReciver(HomeView.this, mBluetoothAdapter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                bluethoothFileReciver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else
                bluethoothFileReciver.execute();
            bluetoothAsyncTaskStarted=true;
        }
    }

    private void startWIfiReciver() {
        if(!wiFiAsyncTaskStarted) {
            WiFiFileReceiver wiFiFileReceiver = new WiFiFileReceiver(HomeView.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                wiFiFileReceiver.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
            } else
                wiFiFileReceiver.execute();
            wiFiAsyncTaskStarted=true;
        }
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
        //when group is formed this method getting call.
    }

    @Override
    public void updateDevice(WifiP2pDevice device) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
    }

    private void settingDialog() {
        final Dialog dialog = new Dialog(HomeView.this,R.style.PauseDialog);
        dialog.setContentView(R.layout.setting_dialog);
        //dialog.setTitle(getResources().getString(R.string.update_profile));

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
        if (requestCode == BLUETOOTH_DISCOVERABLE && resultCode == 400) {
            //Start the Receiver thread.
            if (Build.VERSION.SDK_INT >= 23)
                takeRunTimePermissionForStorage(BLUETOOTH_READ_WRITE_PERMISSION);
            else {
                if(FileUtility.checkSpaceInSDcard())
                    startBluetoothReciver();
                else {
                    showNoSpaceAlert();
                    bluetoothSwitch.setChecked(false);
                }
            }

            bluetoothSwitch.setChecked(true);
        }
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == REQUEST_ENABLE_BT || requestCode == BLUETOOTH_DISCOVERABLE)
                bluetoothSwitch.setChecked(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case BLUETOOTH_READ_WRITE_PERMISSION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startBluetoothReciver();
                }
                break;
            case WIFI_READ_WRITE_PERMISSION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startWIfiReciver();
                }
                break;
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
            Snackbar.make(coordinatorLayout, getResources().getString(R.string.turn_on_bluethooth), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.turn_on), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            turnOnBluetoothIntent();

                        }
                    }).show();
        }
    }

}
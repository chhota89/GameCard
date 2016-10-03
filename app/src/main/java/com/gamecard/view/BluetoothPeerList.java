package com.gamecard.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gamecard.R;
import com.gamecard.adapter.AdapterBluethooth;
import com.gamecard.callback.CallBackBluetooth;
import com.gamecard.callback.ClickListener;
import com.gamecard.utility.BluetoothBroadcastReceiver;
import com.gamecard.utility.FIleSendBluetooth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothPeerList extends AppCompatActivity implements CallBackBluetooth{

    private static final int REQUEST_ENABLE_BT = 120;
    private static final int BLUETOOTH_DISCOVRABLE=121;
    private static final String TAG="BluetoothPeerList";
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 218;
    private static final int ASK_READ_WRITE_PERMISSION = 219;
    Intent fileSendBluetooth;
    IntentFilter bluetoothIntent =new IntentFilter();
    BluetoothBroadcastReceiver bluetoothBroadcastReciver;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> deviceList;
    private RecyclerView recyclerView;
    private AdapterBluethooth adapterBluetooth;
  //  ApplicationInfo applicationInfo;
    List<Object> appList;
    ProgressDialog mProgressDialog;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    String sourceDir, loadLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluethooth_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.title_activity_peer_list));

        progressDialog = new ProgressDialog(BluetoothPeerList.this);
        progressDialog.setMessage("Searching ...");

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);

     //   applicationInfo = getIntent().getParcelableExtra("APPLICATION");
        sourceDir = getIntent().getStringExtra(VideoFragment.SOURCE_DIR);
        loadLabel = getIntent().getStringExtra(VideoFragment.LABEL_NAME);

        deviceList=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.peerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterBluetooth = new AdapterBluethooth(BluetoothPeerList.this, deviceList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                BluetoothDevice device=deviceList.get(position);
                fileSendBluetooth=new Intent(BluetoothPeerList.this, FIleSendBluetooth.class);
                fileSendBluetooth.putExtra("Device",device);
                fileSendBluetooth.putExtra("UUID",getString(R.string.uuid));
                fileSendBluetooth.putExtra("fileToSend",new File(sourceDir));
                fileSendBluetooth.putExtra("resultReciver", new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, final Bundle resultData) {
                        if (resultCode == FIleSendBluetooth.PORT) {
                            if (mProgressDialog != null) {

                                int percentage = resultData.getInt("Progress", 0);
                                mProgressDialog.setProgress(percentage);

                                if (percentage == 100) {
                                    mProgressDialog.hide();
                                    mProgressDialog=null;
                                }
                            }
                            else{
                                //Initialize the progress dialog
                                mProgressDialog = new ProgressDialog(BluetoothPeerList.this);
                                mProgressDialog.setMessage("Sending ..... "+loadLabel);
                                mProgressDialog.setIndeterminate(false);
                                mProgressDialog.setMax(100);
                                mProgressDialog.setProgressNumberFormat(null);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.show();
                            }
                        }
                    }
                });
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();
                //Check for file read and write permission on android M
                if(Build.VERSION.SDK_INT>=23)
                    takeRunTimePermissionForStorage();
                else
                    startService(fileSendBluetooth);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterBluetooth);

         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        else{
            //Device has bluetooth
            if (!mBluetoothAdapter.isEnabled()) {
                //request to turn on bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        bluetoothIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothIntent.addAction(BluetoothDevice.ACTION_FOUND);
    }

    private void takeRunTimePermissionForStorage() {

            //check for ACCESS_COARSE_LOCATION permission
            int hasPermission = ActivityCompat.checkSelfPermission(BluetoothPeerList.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ActivityCompat.checkSelfPermission(BluetoothPeerList.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (hasPermission == PackageManager.PERMISSION_GRANTED && readPermission==PackageManager.PERMISSION_GRANTED) {
                startService(fileSendBluetooth);
                return;
            }

            ActivityCompat.requestPermissions(BluetoothPeerList.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    ASK_READ_WRITE_PERMISSION);
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
                if(Build.VERSION.SDK_INT>=23){
                    //check for ACCESS_COARSE_LOCATION permission
                    int hasPermission = ActivityCompat.checkSelfPermission(BluetoothPeerList.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                        mBluetoothAdapter.startDiscovery();
                        progressDialog.show();
                        return true;
                    }

                    ActivityCompat.requestPermissions(BluetoothPeerList.this,
                            new String[]{
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_COARSE_LOCATION_PERMISSIONS);
                    return true;
                }
                else{
                    progressDialog.show();
                    mBluetoothAdapter.startDiscovery();
                    return true;
                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_OK){
            Toast.makeText(BluetoothPeerList.this,"Bluetooth is enabled",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(BluetoothPeerList.this,"Bluetooth is disabld",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mBluetoothAdapter.startDiscovery();
                } else {
                    Toast.makeText(this,
                            getResources().getString(R.string.permission_failure),
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
            case ASK_READ_WRITE_PERMISSION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(fileSendBluetooth);
                }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bluetoothBroadcastReciver =new BluetoothBroadcastReceiver(this);
        registerReceiver(bluetoothBroadcastReciver, bluetoothIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothBroadcastReciver);
    }

    @Override
    public void addDevice(BluetoothDevice device) {
        progressDialog.hide();
        deviceList.add(device);
        adapterBluetooth.notifyDataSetChanged();
    }

    @Override
    public void deviceState(boolean flag) {
        if(!flag)
            Snackbar.make(coordinatorLayout,"Turn on bluetooth",Snackbar.LENGTH_LONG).setAction("TurnOn", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }).show();
    }
}

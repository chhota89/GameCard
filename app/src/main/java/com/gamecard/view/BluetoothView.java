package com.gamecard.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gamecard.R;
import com.gamecard.adapter.AdapterBluethooth;
import com.gamecard.adapter.AdapterPeerList;
import com.gamecard.callback.CallBackBluetooth;
import com.gamecard.utility.BluetoothBroadcastReciver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothView extends AppCompatActivity implements CallBackBluetooth{

    private static final int REQUEST_ENABLE_BT = 120;
    private static final String TAG="BluetoothView";
    IntentFilter intetntFilter=new IntentFilter();
    BluetoothBroadcastReciver broadcastReciver;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> deviceList;
    private RecyclerView recyclerView;
    private AdapterBluethooth adapterBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluethooth_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceList=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.peerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterBluetooth = new AdapterBluethooth(BluetoothView.this, deviceList);
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
            else{
                getPairedDevice();
            }
        }

        intetntFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intetntFilter.addAction(BluetoothDevice.ACTION_FOUND);
    }

    private void getPairedDevice(){
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        deviceList.addAll(pairedDevices);
        adapterBluetooth.notifyDataSetChanged();
        String abc="kljd";
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
                mBluetoothAdapter.startDiscovery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_OK){
            Toast.makeText(BluetoothView.this,"Bluetooth is enabled",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(BluetoothView.this,"Bluetooth is disabld",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReciver=new BluetoothBroadcastReciver(this);
        registerReceiver(broadcastReciver,intetntFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReciver);
    }

    @Override
    public void addDevice(BluetoothDevice device) {
        deviceList.add(device);
        adapterBluetooth.notifyDataSetChanged();
    }
}

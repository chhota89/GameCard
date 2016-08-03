package com.gamecard.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamecard.R;
import com.gamecard.callback.ClickListener;
import com.gamecard.viewholder.PeerViewHolder;

import java.util.List;

/**
 * Created by bridgeit on 4/7/16.
 */

public class AdapterBluethooth extends RecyclerView.Adapter<PeerViewHolder> {
    List<BluetoothDevice> devices;
    LayoutInflater inflater;
    Context context;
    ClickListener mClickListner;

    public AdapterBluethooth(Context context, List<BluetoothDevice> devices,ClickListener clickListener) {
        this.mClickListner=clickListener;
        this.context=context;
        inflater = LayoutInflater.from(context);
        this.devices = devices;

    }

    private static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case BluetoothDevice.BOND_BONDED:
                return "BOND_BONDED";
            case BluetoothDevice.BOND_NONE:
                return "BOND_NONE";
            case BluetoothDevice.BOND_BONDING:
                return "BOND_BONDING";
            default:
                return "Unknown";

        }
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeerViewHolder(inflater.inflate(R.layout.adapter_peer, parent, false));
    }

    @Override
    public void onBindViewHolder(PeerViewHolder holder, final int position) {
        holder.device_name.setText(devices.get(position).getName());
        holder.device_details.setText(getDeviceStatus(devices.get(position).getBondState()));
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListner.onClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
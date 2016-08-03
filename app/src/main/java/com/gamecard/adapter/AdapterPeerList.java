package com.gamecard.adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamecard.R;
import com.gamecard.viewholder.PeerViewHolder;

import java.util.List;

import static android.R.attr.id;

/**
 * Created by bridgeit on 28/6/16.
 */

public class AdapterPeerList extends RecyclerView.Adapter<PeerViewHolder> {
    List<WifiP2pDevice> devices;
    LayoutInflater inflater;
    Context context;

    public AdapterPeerList(Context context, List<WifiP2pDevice> devices) {

        this.context=context;
        inflater = LayoutInflater.from(context);
        this.devices = devices;

    }

    private static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeerViewHolder(inflater.inflate(R.layout.adapter_peer, parent, false));
    }

    @Override
    public void onBindViewHolder(PeerViewHolder holder, int position) {
        holder.device_name.setText(devices.get(position).deviceName);
        holder.device_details.setText(getDeviceStatus(devices.get(position).status));
        holder.send.setVisibility(View.INVISIBLE);
        if(devices.get(position).status ==WifiP2pDevice.CONNECTED){
            if(Build.VERSION.SDK_INT>=23)
                holder.device_details.setTextColor(ContextCompat.getColor(context,R.color.green));
            else
                holder.device_details.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}

package com.gamecard.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamecard.R;

/**
 * Created by bridgeit on 28/6/16.
 */

public class PeerViewHolder extends RecyclerView.ViewHolder {
    public TextView device_details, device_name;
    public ImageView send;

    public PeerViewHolder(View itemView) {
        super(itemView);
        device_details = (TextView) itemView.findViewById(R.id.device_details);
        device_name = (TextView) itemView.findViewById(R.id.device_name);
        send=(ImageView)itemView.findViewById(R.id.send);
    }
}

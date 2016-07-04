package com.gamecard.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gamecard.R;

/**
 * Created by bridgeit on 27/6/16.
 */

public class MoreViewHolder extends ParentViewHolder {

    public TextView type;
    public Button more;

    public MoreViewHolder(View itemView) {
        super(itemView);
        type = (TextView) itemView.findViewById(R.id.type);
        more=(Button)itemView.findViewById(R.id.more);
    }

}

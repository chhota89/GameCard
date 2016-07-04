package com.gamecard.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamecard.R;

/**
 * Created by bridgeit on 23/6/16.
 */

public class GameInfoViewHoldr extends ParentViewHolder {

    public TextView appName;
    public ImageView appImage;

    public GameInfoViewHoldr(View itemView) {
        super(itemView);
        appName = (TextView) itemView.findViewById(R.id.appName);
        appImage = (ImageView) itemView.findViewById(R.id.appLogo);
    }
}

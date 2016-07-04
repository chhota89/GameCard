package com.gamecard.callback;

import android.view.View;

/**
 * Created by bridgeit on 26/6/16.
 */

public interface ClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}

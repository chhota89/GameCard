package com.gamecard.utility;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by bridgeit on 27/6/16.
 */

public class AnimationUtility {

    public static void animateUp(View view, int height) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", height, 0);
        animator.setDuration(1000);
        animator.start();
    }

    public static void animatedGrid(RecyclerView.ViewHolder holder) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationY", 200, 200);
        animator.setDuration(1000);
        animator.start();
    }
}

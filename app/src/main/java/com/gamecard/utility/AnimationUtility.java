package com.gamecard.utility;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashSet;

/**
 * Created by bridgeit on 27/6/16.
 */

public class AnimationUtility {

    public static void animateUp(View view, int height) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", height, 0);
        animator.setDuration(1000);
        animator.start();
    }

    public static void animated(RecyclerView.ViewHolder holder,boolean goesDown,int postion){
        ObjectAnimator animator=ObjectAnimator.ofFloat(holder.itemView,"translationY",goesDown==true ? 600 :-600,0);
        animator.setDuration(400+(postion*40));
        animator.start();
    }


}

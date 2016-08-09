package com.gamecard.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gamecard.R;
import com.gamecard.utility.AnimationUtility;
import com.gamecard.viewholder.GameInfoViewHoldr;
import com.gamecard.viewholder.MoreViewHolder;
import com.gamecard.viewholder.ParentViewHolder;

import java.util.List;

/**
 * Created by bridgeit on 23/6/16.
 */

public class AdapterDisplayApp extends RecyclerView.Adapter<ParentViewHolder> {

    private final int APPLICATION_TYPE = 0, MORE_TYPE = 1;
    LayoutInflater inflater;
    List<Object> applicationInfos;
    PackageManager packageManager;
    Context context;
    LruCache<String,Drawable> cache;
    private static final String TAG="AdapterDisplayApp";
    private  int previous=0;

    public AdapterDisplayApp(List<Object> applicationInfos, Context context) {
        this.applicationInfos = applicationInfos;
        inflater = LayoutInflater.from(context);
        packageManager = context.getPackageManager();
        this.context=context;

        //Find out maximum memory available to application
        //1024 is used because LruCache constructor takes int in kilobytes
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/4th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;
        Log.d(TAG, "max memory " + maxMemory + " cache size " + cacheSize);

                // LruCache takes key-value pair in constructor
                // key is the string to refer bitmap
                // value is the stored bitmap
        cache = new LruCache<String, Drawable>(cacheSize) /*{
            @Override
            protected int sizeOf(String key, Drawable drawable) {
                // The cache size will be measured in kilobytes
                return drawable.b / 1024;
            }
        }*/;

    }

    @Override
    public ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ParentViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == APPLICATION_TYPE) {
            View viewUserInfo = inflater.inflate(R.layout.adapter_app_display, parent, false);
            viewHolder = new GameInfoViewHoldr(viewUserInfo);
        } else {
            View viewUserInfo = inflater.inflate(R.layout.adapter_category_more, parent, false);
            viewHolder = new MoreViewHolder(viewUserInfo);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ParentViewHolder holder1, int position) {
        if (holder1.getItemViewType() == APPLICATION_TYPE) {
            GameInfoViewHoldr holder = (GameInfoViewHoldr) holder1;
            //AnimationUtility.animatedGrid(holder);
            if (((ApplicationInfo) applicationInfos.get(position)).loadLabel(packageManager).length() > 7)
                holder.appName.setText(((ApplicationInfo) applicationInfos.get(position)).loadLabel(packageManager).toString().substring(0, 7) + "..");
            else
                holder.appName.setText(((ApplicationInfo) applicationInfos.get(position)).loadLabel(packageManager));
            Drawable drawable=cache.get(((ApplicationInfo) applicationInfos.get(position)).packageName);
            if(drawable!=null)
                holder.appImage.setImageDrawable(drawable);
            else {
                drawable=((ApplicationInfo) applicationInfos.get(position)).loadIcon(packageManager);
                cache.put(((ApplicationInfo) applicationInfos.get(position)).packageName, drawable);
                holder.appImage.setImageDrawable(drawable);
            }

            if(position>previous)
                AnimationUtility.animated(holder,true,position);
            else
                AnimationUtility.animated(holder,false,position);

            previous=position;

        } else {
            MoreViewHolder moreViewHolder = (MoreViewHolder) holder1;
            //AnimationUtility.animatedGrid(moreViewHolder);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) moreViewHolder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            String category = (String) applicationInfos.get(position);
            moreViewHolder.type.setText(category);

            moreViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"More clicked",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return applicationInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (applicationInfos.get(position) instanceof ApplicationInfo)
            return APPLICATION_TYPE;
        else if (applicationInfos.get(position) instanceof String)
            return MORE_TYPE;
        else
            return -1;
    }
}

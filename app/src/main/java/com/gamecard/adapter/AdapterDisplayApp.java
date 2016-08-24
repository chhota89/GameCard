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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gamecard.R;
import com.gamecard.model.GameResponseModel;
import com.gamecard.utility.AnimationUtility;
import com.gamecard.viewholder.GameInfoViewHoldr;
import com.gamecard.viewholder.MoreViewHolder;
import com.gamecard.viewholder.ParentViewHolder;

import java.util.List;

/**
 * Created by bridgeit on 23/6/16.
 */

public class AdapterDisplayApp extends RecyclerView.Adapter<ParentViewHolder> {

    private final int APPLICATION_TYPE = 0, MORE_TYPE = 1, SUGGESTION_TYPE=2;
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
        if (viewType == MORE_TYPE) {
            View viewUserInfo = inflater.inflate(R.layout.adapter_category_more, parent, false);
            viewHolder = new MoreViewHolder(viewUserInfo);
        } else {
            //type may be install app or suggestion
            View viewUserInfo = inflater.inflate(R.layout.adapter_app_display, parent, false);
            viewHolder = new GameInfoViewHoldr(viewUserInfo);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ParentViewHolder holder1, int position) {
        if (holder1.getItemViewType() == APPLICATION_TYPE || holder1.getItemViewType()==SUGGESTION_TYPE) {

            if(applicationInfos.get(position) instanceof ApplicationInfo){
                GameInfoViewHoldr holder = (GameInfoViewHoldr) holder1;
                setGameName(holder.appName, String.valueOf(((ApplicationInfo) applicationInfos.get(position)).loadLabel(packageManager)));
                Drawable drawable=cache.get(((ApplicationInfo) applicationInfos.get(position)).packageName);
                if(drawable!=null)
                    holder.appImage.setImageDrawable(drawable);
                else {
                    drawable=((ApplicationInfo) applicationInfos.get(position)).loadIcon(packageManager);
                    cache.put(((ApplicationInfo) applicationInfos.get(position)).packageName, drawable);
                    holder.appImage.setImageDrawable(drawable);
                }

            }else if(applicationInfos.get(position) instanceof GameResponseModel){

                GameInfoViewHoldr holder = (GameInfoViewHoldr) holder1;
                setGameName(holder.appName, ((GameResponseModel) applicationInfos.get(position)).getGametittle());

                Glide.with(context).load(((GameResponseModel) applicationInfos.get(position)).getIconLink()).into(holder.appImage);
            }


        } else if(holder1.getItemViewType() == MORE_TYPE) {
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

        if(position>previous)
            AnimationUtility.animated(holder1,true,position);
        else
            AnimationUtility.animated(holder1,false,position);
        previous=position;

    }

    private void setGameName(TextView textView,String text){

        if (text.length() > 7)
            textView.setText(text.substring(0, 7) + "..");
        else
            textView.setText(text);
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
        else if(applicationInfos.get(position) instanceof GameResponseModel)
            return SUGGESTION_TYPE;
        else
            return -1;
    }
}

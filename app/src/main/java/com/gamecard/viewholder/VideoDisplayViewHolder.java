package com.gamecard.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.gamecard.R;

/**
 * Created by bridgeit on 29/8/16.
 */

public class VideoDisplayViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout videosLayout;
    public VideoView videoDisplay;
    public TextView gameTitle, packageName;
    public ImageView iconImage, apkDownload;

    public VideoDisplayViewHolder(View view) {
        super(view);

        videosLayout = (RelativeLayout) view.findViewById(R.id.videos_layout);
        videoDisplay = (VideoView) view.findViewById(R.id.video_display);
        gameTitle = (TextView) view.findViewById(R.id.game_title);
        packageName = (TextView) view.findViewById(R.id.package_name);
       /* apkLink = (TextView) view.findViewById(R.id.apk_link);*/
        iconImage = (ImageView) view.findViewById(R.id.icon_image);
        apkDownload = (ImageView) view.findViewById(R.id.apkDownload);

    }
}

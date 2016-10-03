package com.gamecard.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.gamecard.R;
import com.google.android.exoplayer.VideoSurfaceView;

/**
 * Created by bridgeit on 29/8/16.
 */

public class VideoDisplayViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout videosLayout;
    public VideoSurfaceView videoView;
    public TextView gameTitle, packageName;
    public ImageView iconImage, apkDownload, like, comment;

    public VideoDisplayViewHolder(View view) {
        super(view);

        videosLayout = (RelativeLayout) view.findViewById(R.id.videos_layout);
        videoView = (VideoSurfaceView) view.findViewById(R.id.video_display);
        gameTitle = (TextView) view.findViewById(R.id.game_title);
        packageName = (TextView) view.findViewById(R.id.package_name);
       /* apkLink = (TextView) view.findViewById(R.id.apk_link);*/
        iconImage = (ImageView) view.findViewById(R.id.icon_image);
        apkDownload = (ImageView) view.findViewById(R.id.apkDownload1);
        like = (ImageView) view.findViewById(R.id.like);
        comment = (ImageView) view.findViewById(R.id.comment);

    }
}

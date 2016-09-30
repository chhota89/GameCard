package com.gamecard.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gamecard.R;
import com.gamecard.exoplayer.DashRendererBuilder;
import com.gamecard.exoplayer.DemoPlayer;
import com.gamecard.exoplayer.DemoUtil;
import com.gamecard.exoplayer.LoadVedioLink;
import com.gamecard.exoplayer.WidevineTestMediaDrmCallback;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.utility.DownloadService;
import com.gamecard.view.ImageFragment;
import com.gamecard.view.VideoFragment;
import com.gamecard.viewholder.GameInfoViewHoldr;
import com.gamecard.viewholder.VideoDisplayViewHolder;
import com.google.android.exoplayer.VideoSurfaceView;

import java.util.LinkedList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by bridgeit on 29/8/16.
 */

public class AdapterVideoDisplay extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SurfaceHolder.Callback, DemoPlayer.Listener{

    private static final int VIDEO_TYPE = 0;
    private static final int VIDEO_DISPLAY_TYPE = 1;
    private static final int IMAGE_TYPE = 2;
    private static boolean downloadStarted = false;
//    private static final String TAG = AdapterVideoDisplay.class.getSimpleName();
    private List<VideoDisplayModel> mVideos;
    private GameResponseModel videos1;
    private ImageView mApkDownload, iconImage;
    private TextView gameTitle;
    private Context mContext;
    private String inputTitle1, inputTitle, title, inputApk, apklink;
    private static int progress;
    public RelativeLayout videosLayout1;
    private int id = 1, j;
    private String[] perms = {Manifest.permission_group.STORAGE};
    private int permsRequestCode = 200;
    private SharedPreferences sharedPreferences;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private DemoPlayer mPlayer;
    private boolean autoPlay = true;
    private MediaController mMediaController;
    private boolean playerNeedsPrepare;
    private VideoSurfaceView videoView, videoView1, videoView2;
    private DashRendererBuilder mDashRendererBuilder;
    FrameLayout mFrameLayout;
    String userAgent;
    private List<String> mUrlList;
    private List<String> videoList1 , videoListLink, video_listId, video_listUrl;
    String[] strArray, strArray1;
    private String mVideo_id;
    private String mGameTitle, mIconLink;

    public AdapterVideoDisplay(Context context, String video_id, String game_title, String icon_link,
                               List<VideoDisplayModel> videos2, DemoPlayer player, List<String> urlList,
                               DashRendererBuilder dashRendererBuilder, FrameLayout frameLayout,
                               MediaController mediaController){

        this.mContext = context;
        this.mVideo_id = video_id;
        this.mGameTitle = game_title;
        this.mIconLink = icon_link;
        this.mVideos = videos2;
        this.mPlayer = player;
        this.mUrlList = urlList;
        this.mDashRendererBuilder = dashRendererBuilder;
        this.mFrameLayout = frameLayout;
        this.mMediaController = mediaController;

        userAgent = DemoUtil.getUserAgent(context);
    //    mMediaController = new MediaController(context);
        try{
            player.addListener(this);
            mMediaController.setMediaPlayer(player.getPlayerControl());
            mMediaController.setEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        if(mVideos.size() > 1) {
            return mVideos.size();
        } else {
            return mUrlList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch(position) {
            case 0:
                if(mVideo_id != "") {
                    return VIDEO_TYPE;
                }
            case 2:
                if(mVideo_id == "" && position == 0){
                    return IMAGE_TYPE;
                }
            default:
                return VIDEO_DISPLAY_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == VIDEO_TYPE) {

            View viewUserInfo = inflater.inflate(R.layout.activity_show_video_details, parent, false);
            return new GameInfoViewHoldr(viewUserInfo);
        }
        else if(viewType == IMAGE_TYPE) {

            View viewUserInfo = inflater.inflate(R.layout.fragment_image_layout, parent, false);
            return new VideoDisplayViewHolder(viewUserInfo);
        }
        else {
            View viewUserInfo = inflater.inflate(R.layout.activity_show_video_details, parent, false);
            return new VideoDisplayViewHolder(viewUserInfo);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        try {
            if (holder.getItemViewType() == VIDEO_TYPE) {

                iconImage = (ImageView) holder.itemView.findViewById(R.id.icon_image);
                gameTitle = (TextView) holder.itemView.findViewById(R.id.game_title);
                mApkDownload = (ImageView) holder.itemView.findViewById(R.id.apkDownload1);

                setGameName(gameTitle, mGameTitle);
                Glide.with(mContext).load(mIconLink).into(iconImage);

                mMediaController.hide();
                final String video = mVideo_id;

                strArray1 = new String[] {video};
                video_listId = new LinkedList<>();
                video_listUrl = new LinkedList<>();

                new LoadVedioLink(mContext) {
                    @Override
                    protected void onPostExecute(String s1) {
                        super.onPostExecute(s1);
                        if(s1 != null) {
                            video_listId.add(video);
                            video_listUrl.add(s1);

                            if(video_listUrl != null){
                                loadVideo(video_listId, video_listUrl, holder, position);
                            }
                        }
                    }
                }.execute(strArray1);
            }
            else if(holder.getItemViewType() == IMAGE_TYPE) {
               /* ImageFragment.newInstance(mUrlList.get(position)).onStart();
                  mUrlList.add(0, ImageFragment.newInstance(mUrlList.get(position)).getString(0));
                   //   Glide.with(mContext).load(mUrlList.get(0)).into(appImage);
                }*/
                final String pos = mUrlList.get(0);
                ImageView appImage=(ImageView) holder.itemView.findViewById(R.id.appImage);
                Glide.with(mContext).load(pos).into(appImage);
                ImageFragment.newInstance(mUrlList.get(position));
            }
            else{

                mMediaController.hide();
                VideoDisplayViewHolder holder2 = (VideoDisplayViewHolder) holder;
                videoList1 = new LinkedList<String>();
                videoListLink = new LinkedList<String>();

                final String videoListId = mVideos.get(position).getVedioLink();
                strArray = new String[] {videoListId};

                new LoadVedioLink(mContext) {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if(s != null) {
                            int i = 0;

                            try {
                                j = position;

                                while (j != i) {
                                    videoList1.add(0, "Continue Displaying");
                                    videoListLink.add(0, "Continue Displaying");
                                    i++;
                                }

                                videoList1.add(j, videoListId);
                                videoListLink.add(j, s);

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if(videoListLink != null){
                                loadVideos(videoList1, videoListLink, holder, position);
                            }
                        }
                    }
                }.execute(strArray);

                setGameName(holder2.gameTitle, mVideos.get(position).getGameTitle());
                Glide.with(mContext).load(mVideos.get(position).getIconLink()).into(holder2.iconImage);

                holder2.apkDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            apklink = (String) mVideos.get(position).getApkLink();
                            inputApk = apklink.replace(" ", "%20");
                            title = mVideos.get(position).getGameTitle();
                            inputTitle = title.replace(" ", "_");
                            inputTitle1 = inputTitle.trim();
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        if (isExternalStorageWritable()) {

                            // prepare intent which is triggered if the notification is selected
                            Intent intent1 = new Intent(Intent.ACTION_VIEW);

                            try {
                                intent1.setDataAndType(Uri.parse("file://" +
                                        Environment.getExternalStorageDirectory()
                                        + "/GameCenter/" + inputTitle1 + ".apk"),
                                        "application/vnd.android.package-archive");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final PendingIntent pIntent = PendingIntent.getActivity(mContext,
                                    (int) System.currentTimeMillis(), intent1, 0);

                            Intent intent = new Intent(mContext, DownloadService.class);
                            //* Send optional extras to Download IntentService *//
                            intent.putExtra("url", inputApk);
                            intent.putExtra("title", inputTitle1);
                            intent.putExtra("receiver", new ResultReceiver(new Handler()) {
                                @Override
                                protected void onReceiveResult(int resultCode, Bundle resultData) {
                                    super.onReceiveResult(resultCode, resultData);
                                    switch (resultCode) {
                                        case DownloadService.STATUS_RUNNING:
                                            downloadStarted = true;

                                            mNotifyManager = (NotificationManager) mContext
                                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                                            mBuilder = new NotificationCompat.Builder(mContext);
                                            mBuilder.setContentTitle("Download")
                                                    .setContentText("Download in progress")
                                                    .setOngoing(true)
                                                    .setProgress(100, 0, false)
                                                    .setSmallIcon(R.drawable.ic_download)
                                                    .setContentIntent(pIntent)
                                                    .setAutoCancel(true);

                                            // Issues the notification
                                            mNotifyManager.notify(id, mBuilder.build());
                                            Toast.makeText(mContext, "Download in PROGRESS...",
                                                    Toast.LENGTH_LONG).show();
                                            break;
                                        case DownloadService.STATUS_FINISHED:
                                            //* Hide progress & extract result from bundle *//*
                                            mBuilder.addAction(0, " \t\t\t\t\t\t\t\t\t\t\t\t\t\t Install",
                                                    pIntent);
                                            Toast.makeText(mContext, "Download Complete",
                                                    Toast.LENGTH_LONG).show();

                                            mBuilder.setContentText("Download Complete");
                                            // Removes the progress bar
                                            mBuilder.setProgress(0, 0, false);
                                            mNotifyManager.notify(id, mBuilder.build());

                                            String results = resultData.getString("result");
                                            //* Update with result *//*
                                            Toast.makeText(mContext, results, Toast.LENGTH_LONG).show();
                                            break;
                                        case DownloadService.STATUS_ERROR:
                                            //* Handle the error *//*
                                            String error = resultData.getString(Intent.EXTRA_TEXT);
                                            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                                            break;
                                        case DownloadService.UPDATE_PROGRESS:

                                            mBuilder.setProgress(100, progress, false);
                                            mNotifyManager.notify(id, mBuilder.build());

                                            progress = resultData.getInt("progress");
                                            if (progress == 100) {
                                                try {
                                                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                                    // view apk file
                                                    intent1.setDataAndType(Uri.parse("file://" +
                                                            Environment.getExternalStorageDirectory()
                                                            + "/GameCenter/" + inputTitle1 + ".apk"),
                                                            "application/vnd.android.package-archive");
                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mContext.startActivity(intent1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            break;
                                        }
                                    }
                                });

                                try {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        requestPermissions((Activity) mContext, perms, permsRequestCode);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            mContext.startService(intent);
                                if (downloadStarted) {
                                    Toast.makeText(mContext, "PLEASE WAIT...", Toast.LENGTH_LONG).show();
                                }
                        }else {
                            Toast.makeText(mContext, "No sufficient memory for storing", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                final View v1 = holder2.apkDownload;
                final View v2 = holder2.gameTitle;
                final View v3 = holder2.comment;
                final View v4 = holder2.iconImage;
                final View v5 = holder2.like;


              /*  if(v1.getVisibility() == View.VISIBLE && v2.getVisibility() == View.VISIBLE
                        && v3.getVisibility() == View.VISIBLE && v4.getVisibility() == View.VISIBLE
                        && v5.getVisibility() == View.VISIBLE)
                {
                    Animation out = AnimationUtils.makeOutAnimation(mContext,true);
                    v1.startAnimation(out);
                    v1.setVisibility(View.INVISIBLE);

                    v2.startAnimation(out);
                    v2.setVisibility(View.INVISIBLE);

                    v3.startAnimation(out);
                    v3.setVisibility(View.INVISIBLE);

                    v4.startAnimation(out);
                    v4.setVisibility(View.INVISIBLE);

                    v5.startAnimation(out);
                    v5.setVisibility(View.INVISIBLE);
                }*/

                holder2.videosLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Animation in = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                        v1.startAnimation(in);
                        v2.startAnimation(in);
                        v3.startAnimation(in);
                        v4.startAnimation(in);
                        v5.startAnimation(in);
                    }
                });

                holder2.videosLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                            v5.clearAnimation();
                            v4.clearAnimation();
                            v3.clearAnimation();
                            v2.clearAnimation();
                            v1.clearAnimation();

                        return true;
                    }
                });
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadVideo(List<String> videoList1, List<String> videoListLink,
                           RecyclerView.ViewHolder holder, final int position){
        try{

            VideoFragment.vedioPlay = false;

            videoView1 = (VideoSurfaceView) holder.itemView.findViewById(R.id.video_display);
            videosLayout1 = (RelativeLayout) holder.itemView.findViewById(R.id.videos_layout);

            mDashRendererBuilder.setContentId(videoList1.get(position));
            mDashRendererBuilder.setUrl(videoListLink.get(position));
            mDashRendererBuilder.setMediaDrmCallback(new WidevineTestMediaDrmCallback(videoList1.get(position)));

            mMediaController.setAnchorView(videoView1);
            mPlayer.seekTo(0);
            mPlayer.setSurface(videoView1.getHolder().getSurface());
            mPlayer.prepare();

            videoView1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        toggleControlsVisibility();
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVideos(List<String> videoList1, List<String> videoListLink,
                            RecyclerView.ViewHolder holder, final int position){

        try {
            VideoDisplayViewHolder holder2 = (VideoDisplayViewHolder) holder;

            VideoFragment.vedioPlay = false;

            this.videoView = holder2.videoView;

            mDashRendererBuilder.setContentId(videoList1.get(position));
            mDashRendererBuilder.setUrl(videoListLink.get(position));
            mDashRendererBuilder.setMediaDrmCallback(new WidevineTestMediaDrmCallback(videoList1.get(position)));

            mMediaController.setAnchorView(holder2.videoView);
            mPlayer.seekTo(0);
            mPlayer.setSurface(holder2.videoView.getHolder().getSurface());
            mPlayer.prepare();

            holder2.videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        toggleControlsVisibility();
                    }
                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setGameName(TextView textView, String text) {
        textView.setText(text);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean storageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    private boolean shouldWeAsk(String permission){
        return (sharedPreferences.getBoolean(permission, true));
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        if (mPlayer != null) {
            mPlayer.setSurface(surfaceHolder.getSurface());
            maybeStartPlayback();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mPlayer != null) {
            mPlayer.blockingClearSurface();
        }
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        /*if (playbackState == com.google.android.exoplayer.ExoPlayer.STATE_ENDED) {
            showControls();
        }*/

     /*   Log.i(TAG, "onStateChanged: ----------------------------------------");
        if(videoView.getVisibility() == View.INVISIBLE) {
            mMediaController.hide();
        }*/

    }

    @Override
    public void onError(Exception e) {
        playerNeedsPrepare = true;
        //showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        //shutterView.setVisibility(View.GONE);
        try {
            videoView.setVideoWidthHeightRatio(
                    height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void maybeStartPlayback() {

        if (autoPlay && (mPlayer.getSurface().isValid()
                || mPlayer.getSelectedTrackIndex(DemoPlayer.TYPE_VIDEO) == DemoPlayer.DISABLED_TRACK)) {
            mPlayer.setPlayWhenReady(true);
            autoPlay = false;
        }
    }

    private void showControls() {
        mMediaController.show(0);
    }

    private void toggleControlsVisibility() {

        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show(0);
        }
    }

    public void delete(int position) {
        mUrlList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mUrlList.size());
    }
}
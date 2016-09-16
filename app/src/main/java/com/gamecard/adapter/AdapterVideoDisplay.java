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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gamecard.R;
import com.gamecard.exoplayer.DashRendererBuilder;
import com.gamecard.exoplayer.DemoPlayer;
import com.gamecard.exoplayer.DemoUtil;
import com.gamecard.exoplayer.WidevineTestMediaDrmCallback;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.utility.DownloadService;
import com.gamecard.view.YouTubeFragment;
import com.gamecard.viewholder.GameInfoViewHoldr;
import com.gamecard.viewholder.VideoDisplayViewHolder;
import com.google.android.exoplayer.VideoSurfaceView;

import java.util.List;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by bridgeit on 29/8/16.
 */

public class AdapterVideoDisplay extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SurfaceHolder.Callback, DemoPlayer.Listener{

    private static final int VIDEO_TYPE = 0;
    private static final int VIDEO_DISPLAY_TYPE = 1;
    private static boolean downloadStarted = false;
    private static final String TAG = AdapterVideoDisplay.class.getSimpleName();
    private List<VideoDisplayModel> videos2;
    private Context context;
    private String sourceDir, packageName;
    private String inputTitle1, inputTitle, title, inputApk, apklink;
    private CharSequence loadLabel;
    private static int progress;
    private ProgressBar mProgress;
    private TextView show, showPercentage;
    private int id = 1;
    private String[] perms = {Manifest.permission_group.STORAGE};
    private int permsRequestCode = 200;
    private SharedPreferences sharedPreferences;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private DemoPlayer player;
    private boolean autoPlay = true;
    private MediaController mediaController;
    private boolean playerNeedsPrepare;
    private VideoSurfaceView videoView;
    private DashRendererBuilder dashRendererBuilder;
  //  private IVideoDisplay iVideoDisplay;
    FrameLayout frameLayout;
    String userAgent;
    List<String> contentIdList, contentUriList;

    public AdapterVideoDisplay(Context context, List<VideoDisplayModel> videos2,
                               String sourceDir, CharSequence loadLabel, String packageName,
                               List<String> list, List<String> contentUriList,
                               DemoPlayer player, DashRendererBuilder dashRendererBuilder,
                               FrameLayout frameLayout){

        this.videos2 = videos2;
        this.context = context;
        this.sourceDir = sourceDir;
        this.loadLabel = loadLabel;
        this.packageName = packageName;
        userAgent= DemoUtil.getUserAgent(context);
        this.frameLayout = frameLayout;
   //     this.iVideoDisplay=iVideoDisplay;

        this.contentIdList =list;
        this.contentUriList=contentUriList;
        this.player=player;
        this.dashRendererBuilder=dashRendererBuilder;
        mediaController = new MediaController(context);
        //mediaController.setAnchorView(holder.root);

        try{
            player.addListener(this);
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return videos2.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch(position) {
            case 0:
                return VIDEO_TYPE;
            default:
                return VIDEO_DISPLAY_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIDEO_TYPE) {

            View viewUserInfo = inflater.inflate(R.layout.activity_app_description, parent, false);
            return new GameInfoViewHoldr(viewUserInfo);
        }
        else {
            View viewUserInfo = inflater.inflate(R.layout.activity_show_video_details, parent, false);
            return new VideoDisplayViewHolder(viewUserInfo);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {
            if (holder.getItemViewType() == VIDEO_TYPE) {

               /* GameInfoViewHoldr holder1 = (GameInfoViewHoldr) holder;
                setGameName(holder1.appName,((GameResponseModel) videos1.get(position)).getGametittle());
                Glide.with(context).load(((GameResponseModel) videos1.get(position)).getIconLink())
                .into(holder1.appImage);*/

            }
            else{

                VideoDisplayViewHolder holder2 = (VideoDisplayViewHolder) holder;

             /*   holder2.videoDisplay.setVideoURI(Uri.parse(videos2.get(position).getVedioLink()));
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(holder2.videoDisplay);
                holder2.videoDisplay.setMediaController(mediaController);
                holder2.videoDisplay.start();*/

              /*  YouTubeFragment.newInstance(videos2.get(position).getVedioLink(), sourceDir,
                        loadLabel, packageName);
*/

                try {
                    YouTubeFragment.vedioPlay = false;
                  //  iVideoDisplay.showingItemPostion(position);
                    this.videoView = holder2.videoView;
                    dashRendererBuilder.setContentId(contentIdList.get(position));
                    dashRendererBuilder.setUrl(contentUriList.get(position));
                    dashRendererBuilder.setMediaDrmCallback(new WidevineTestMediaDrmCallback(contentIdList.get(position)));
                    mediaController.setAnchorView(holder2.videosLayout);
                    player.seekTo(0);
                    player.setSurface(holder2.videoView.getHolder().getSurface());
                    player.prepare();

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

                setGameName(holder2.gameTitle, videos2.get(position).getGameTitle());

                setGameName(holder2.packageName, videos2.get(position).getPackageName());

                Glide.with(context).load(videos2.get(position).getIconLink()).into(holder2.iconImage);

                holder2.apkDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            apklink = (String) videos2.get(position).getApkLink();
                            inputApk = apklink.replace(" ", "%20");
                            title = videos2.get(position).getGameTitle();
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

                            final PendingIntent pIntent = PendingIntent.getActivity(context,
                                    (int) System.currentTimeMillis(), intent1, 0);

                            Intent intent = new Intent(context, DownloadService.class);
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

                                            mNotifyManager = (NotificationManager) context
                                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                                            mBuilder = new NotificationCompat.Builder(context);
                                            mBuilder.setContentTitle("Download")
                                                    .setContentText("Download in progress")
                                                    .setOngoing(true)
                                                    .setProgress(100, 0, false)
                                                    .setSmallIcon(R.drawable.ic_download)
                                                    .setContentIntent(pIntent)
                                                    .setAutoCancel(true);

                                            // Issues the notification
                                            mNotifyManager.notify(id, mBuilder.build());
                                            Toast.makeText(context, "Download in PROGRESS...",
                                                    Toast.LENGTH_LONG).show();
                                            break;
                                        case DownloadService.STATUS_FINISHED:
                                            //* Hide progress & extract result from bundle *//*
                                            mBuilder.addAction(0, " \t\t\t\t\t\t\t\t\t\t\t\t\t\t Install",
                                                    pIntent);
                                            Toast.makeText(context, "Download Complete",
                                                    Toast.LENGTH_LONG).show();

                                            mBuilder.setContentText("Download Complete");
                                            // Removes the progress bar
                                            mBuilder.setProgress(0, 0, false);
                                            mNotifyManager.notify(id, mBuilder.build());

                                            String results = resultData.getString("result");
                                            //* Update with result *//*
                                            Toast.makeText(context, results, Toast.LENGTH_LONG).show();
                                            break;
                                        case DownloadService.STATUS_ERROR:
                                            //* Handle the error *//*
                                            String error = resultData.getString(Intent.EXTRA_TEXT);
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
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
                                                    context.startActivity(intent1);
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
                                        requestPermissions((Activity) context, perms, permsRequestCode);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                context.startService(intent);
                                if (downloadStarted) {
                                    Toast.makeText(context, "PLEASE WAIT...", Toast.LENGTH_LONG).show();
                                }
                        }else {
                            Toast.makeText(context, "No sufficient memory for storing", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                final View v1 = holder2.apkDownload;
                final View v2= holder2.gameTitle;
                final View v3 = holder2.packageName;
                final View v4 = holder2.iconImage;

                if(v1.getVisibility() == View.VISIBLE && v2.getVisibility() == View.VISIBLE
                        && v3.getVisibility() == View.VISIBLE && v4.getVisibility() == View.VISIBLE)
                {
                    Animation out = AnimationUtils.makeOutAnimation(context,true);
                    v1.startAnimation(out);
                    v1.setVisibility(View.INVISIBLE);

                    v2.startAnimation(out);
                    v2.setVisibility(View.INVISIBLE);

                    v3.startAnimation(out);
                    v3.setVisibility(View.INVISIBLE);

                    v4.startAnimation(out);
                    v4.setVisibility(View.INVISIBLE);
                }

                holder2.videosLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                        v1.startAnimation(in);
                        v1.setVisibility(View.VISIBLE);

                        v2.startAnimation(in);
                        v2.setVisibility(View.VISIBLE);

                        v3.startAnimation(in);
                        v3.setVisibility(View.VISIBLE);

                        v4.startAnimation(in);
                        v4.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setGameName(TextView textView, String text){
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
        if (player != null) {
            player.setSurface(surfaceHolder.getSurface());
            maybeStartPlayback();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == com.google.android.exoplayer.ExoPlayer.STATE_ENDED) {
            showControls();
        }
    }

    @Override
    public void onError(Exception e) {
        playerNeedsPrepare = true;
        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        //shutterView.setVisibility(View.GONE);
        videoView.setVideoWidthHeightRatio(
                height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
    }

    private void maybeStartPlayback() {
        if (autoPlay && (player.getSurface().isValid()
                || player.getSelectedTrackIndex(DemoPlayer.TYPE_VIDEO) == DemoPlayer.DISABLED_TRACK)) {
            player.setPlayWhenReady(true);
            autoPlay = false;
        }
    }

    private void showControls() {
        mediaController.show(0);
        //debugRootView.setVisibility(View.VISIBLE);
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
            //debugRootView.setVisibility(View.GONE);
        } else {
            mediaController.show(0);
        }
    }

   /* public interface IVideoDisplay{
        void showingItemPostion(int postion);
    }*/
}
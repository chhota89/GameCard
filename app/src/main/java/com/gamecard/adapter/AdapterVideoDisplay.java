package com.gamecard.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
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

import com.bumptech.glide.Glide;
import com.gamecard.R;
import com.gamecard.exoplayer.DashRendererBuilder;
import com.gamecard.exoplayer.DemoPlayer;
import com.gamecard.exoplayer.DemoUtil;
import com.gamecard.exoplayer.LoadVedioLink;
import com.gamecard.exoplayer.WidevineTestMediaDrmCallback;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.view.AppDescriptionActivity;
import com.gamecard.view.ImageFragment;
import com.gamecard.view.VideoFragment;
import com.gamecard.viewholder.GameInfoViewHoldr;
import com.gamecard.viewholder.VideoDisplayViewHolder;
import com.google.android.exoplayer.VideoSurfaceView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bridgeit on 29/8/16.
 */

public class AdapterVideoDisplay extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SurfaceHolder.Callback, DemoPlayer.Listener{

    private static final int VIDEO_TYPE = 0;
    private static final int VIDEO_DISPLAY_TYPE = 1;
    private static final int IMAGE_TYPE = 2;
    private List<VideoDisplayModel> mVideos;
    private GameResponseModel videos1;
    private ImageView mApkDownload, iconImage;
    private TextView gameTitle;
    private Context mContext;
    private String inputTitle1, inputTitle, title, inputApk, apklink, userAgent;
    public RelativeLayout videosLayout1;
    private int j;
    private DemoPlayer mPlayer;
    private boolean autoPlay = true;
    private MediaController mMediaController;
    private boolean playerNeedsPrepare;
    private VideoSurfaceView videoView, videoView1;
    private DashRendererBuilder mDashRendererBuilder;
    FrameLayout mFrameLayout;
    private List<String> mUrlList;
    private List<String> videoList1 , videoListLink, video_listId, video_listUrl;
    String[] strArray, strArray1;
    private String mVideo_id, mGameTitle, mIconLink;

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

                video_listId = new LinkedList<>();
                video_listUrl = new LinkedList<>();

                LoadVedioLink loadVedioLink1 = new LoadVedioLink(mContext) {
                    @Override
                    protected void onPostExecute(String videoUrl) {
                        super.onPostExecute(videoUrl);
                        if(videoUrl != null) {
                            video_listId.add(mVideo_id);
                            video_listUrl.add(videoUrl);
                            if(video_listUrl != null){
                                loadVideo(video_listId, video_listUrl, holder, position);
                            }
                        }
                    }
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
                    loadVedioLink1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mVideo_id);
                }else{
                    loadVedioLink1.execute(mVideo_id);
                }
            }
            else if(holder.getItemViewType() == IMAGE_TYPE) {

                final String pos = mUrlList.get(0);
                ImageView appImage=(ImageView) holder.itemView.findViewById(R.id.appImage);
                Glide.with(mContext).load(pos).into(appImage);
                ImageFragment.newInstance(mUrlList.get(position));
            }
            else {
                mMediaController.hide();
                VideoDisplayViewHolder holder2 = (VideoDisplayViewHolder) holder;
                videoList1 = new LinkedList<String>();
                videoListLink = new LinkedList<String>();

                final String videoId = mVideos.get(position).getVedioLink();

                LoadVedioLink loadVedioLink = new LoadVedioLink(mContext) {
                    @Override
                    protected void onPostExecute(String videoLink) {
                        super.onPostExecute(videoLink);
                        if(videoLink != null) {
                            int i = 0;
                            try {
                                j = position;
                                while (j != i) {
                                    videoList1.add(0, "Continue Displaying");
                                    videoListLink.add(0, "Continue Displaying");
                                    i++;
                                }
                                videoList1.add(j, videoId);
                                videoListLink.add(j, videoLink);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(videoListLink != null) {
                                loadVideos(videoList1, videoListLink, holder, position);
                            }
                        }
                    }
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
                    loadVedioLink.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, videoId);
                }else{
                    loadVedioLink.execute(videoId);
                }

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

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        AppDescriptionActivity activity = new AppDescriptionActivity();
                        try {
                            activity.performDownloads(inputApk, inputTitle1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                final View v1 = holder2.apkDownload;
                final View v2 = holder2.gameTitle;
                final View v3 = holder2.comment;
                final View v4 = holder2.iconImage;
                final View v5 = holder2.like;

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
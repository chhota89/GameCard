package com.gamecard.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.gamecard.R;
import com.gamecard.adapter.AdapterVideoDisplay;
import com.gamecard.callback.CallbackRestResponse;
import com.gamecard.controller.RestCall;
import com.gamecard.exoplayer.DashRendererBuilder;
import com.gamecard.exoplayer.DemoPlayer;
import com.gamecard.exoplayer.DemoUtil;
import com.gamecard.exoplayer.EventLogger;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.utility.YoutubeIdConverter;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment {


    private static final String YOUTUBE_FULL_LINK = "YOUTUBE_FULL_LINK";
    private static final String URL_LIST = "URL_LIST";
    private static final String TAG = "VideoFragmentActivity";
    public static final String SOURCE_DIR = "SOURCE_DIR";
    public static final String LABEL_NAME = "LABEL_NAME";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String GAME_TITLE = "GAME_TITLE";
    public static final String ICON_LINK = "ICON_LINK";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    public static String DEVELOPER_KEY = "AIzaSyDm3UWKKI1H6Mqhu5O5_vfzI5mlTt1h6II";

    static MediaController mMediaController;
    private List<String> mUrlList;
    private String mPackage_name, mGameTitle, mIconLink, mVideo_id;
    private FragmentActivity myContext;
    private List<VideoDisplayModel> videoList;
    RecyclerView recyclerView;
    private AdapterVideoDisplay adapter;
    private DemoPlayer player;
    private long playerPosition;
    private EventLogger eventLogger;
    private boolean playerNeedsPrepare;
    private boolean autoPlay = true;
    String mUserAgent, mVideoId;
    private DashRendererBuilder dashRendererBuilder;
    public static boolean vedioPlay = true;
    public static int positionToPlay = 0;
    FrameLayout frameLayout;


    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1          Parameter 1.
     * @param mediaController
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, List<String> urlList, String sourceDir, CharSequence labelName,
                                            String packageName1, String gameTitle, String iconLink,
                                            MediaController mediaController) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(YOUTUBE_FULL_LINK, param1);
        args.putStringArrayList(URL_LIST, (ArrayList<String>) urlList);
        args.putString(SOURCE_DIR, sourceDir);
        args.putCharSequence(LABEL_NAME, labelName);
        args.putString(PACKAGE_NAME, packageName1);
        args.putString(GAME_TITLE, gameTitle);
        args.putString(ICON_LINK, iconLink);

        fragment.setArguments(args);
        mMediaController = mediaController;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {

        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;
        }

        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideo_id = getArguments().getString(YOUTUBE_FULL_LINK);
            mUrlList = getArguments().getStringArrayList(URL_LIST);
            mVideoId = YoutubeIdConverter.getYoutubeVideoId(mVideo_id);
            mPackage_name = getArguments().getString(PACKAGE_NAME);
            mGameTitle = getArguments().getString(GAME_TITLE);
            mIconLink = getArguments().getString(ICON_LINK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_video_display, container, false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(myContext,
                LinearLayoutManager.VERTICAL, false);

        mUserAgent = DemoUtil.getUserAgent(myContext);
        prepareExoPlayer();

        //Initializing the RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.videoDisplayList);
        frameLayout = (FrameLayout) view.findViewById(R.id.youtube_layout);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RestCall restCall = new RestCall(myContext);
        restCall.getVideoDisplayList(mPackage_name, new CallbackRestResponse() {

            @Override
            public void onResponse(List<VideoDisplayModel> videoResponseModel) {
                Log.i(TAG, "onResponseCalled:.");
                videoList = videoResponseModel;
                videoList.add(0, null);
                adapter = new AdapterVideoDisplay(myContext, mVideo_id, mGameTitle, mIconLink,
                        videoList, player, mUrlList, dashRendererBuilder, frameLayout, mMediaController);
                recyclerView.setAdapter(adapter);
                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                            Log.i(TAG, "onScrollStateChanged:scroll ");
                            if(mMediaController.isShowing())
                                mMediaController.hide();

                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            //Call your method here for next set of data
                            int position = linearLayoutManager.findFirstVisibleItemPosition();
                            Rect rect = new Rect();
                            linearLayoutManager.findViewByPosition(position).getGlobalVisibleRect(rect);

                            int lastPostion = linearLayoutManager.findLastVisibleItemPosition();
                            Rect lastRect = new Rect();
                            linearLayoutManager.findViewByPosition(lastPostion).getGlobalVisibleRect(lastRect);


                            if (rect.height() > lastRect.height()) {

                                if (positionToPlay != position) {
                                    vedioPlay = true;
                                    positionToPlay = position;
                                    adapter.notifyItemChanged(position);
                                }
                            } else if (positionToPlay != lastPostion) {
                                vedioPlay = true;
                                positionToPlay = lastPostion;
                                adapter.notifyItemChanged(lastPostion);
                            }

                            Log.i(TAG, "onScrollStateChanged: rect " + rect.height() + "  " + rect.width());
                            Log.i(TAG, "onScrollStateChanged: rect " + lastRect.height() + "  " + lastRect.width());

                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onErrorCalled:");
            }
        });

        return view;
    }

    private void prepareExoPlayer() {
        if (player == null) {

            dashRendererBuilder = new DashRendererBuilder(mUserAgent);
            player = new DemoPlayer(dashRendererBuilder);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;

            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        positionToPlay = 0;
        vedioPlay = true;
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void pausePlayer(){
        player.stopDemoPlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            //eventLogger.endSession();
            eventLogger = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
package com.gamecard.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gamecard.R;
import com.gamecard.adapter.AdapterVideoDisplay;
import com.gamecard.callback.CallbackRestResponse;
import com.gamecard.controller.RestCall;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.utility.YoutubeIdConverter;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.List;


public class YouTubeFragment extends Fragment {


    private static final String YOUTUBE_FULL_LINK = "YOUTUBE_FULL_LINK";
    private static final String TAG = "AppDetailsActivity";
    public static final String SOURCE_DIR = "SOURCE_DIR";
    public static final String LABEL_NAME = "LABEL_NAME";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    public static String DEVELOPER_KEY = "AIzaSyDm3UWKKI1H6Mqhu5O5_vfzI5mlTt1h6II";
    String videoid;
    YouTubePlayerFragment playerFragment;
    CoordinatorLayout coordinatorLayout;
    PackageManager packageManager;
    List<String> sharePackageSet;
    private String mVideo_id;
    private String mSource_dir;
    private String mPackage_name;
    private CharSequence mLabel_name;
    private FragmentActivity myContext;
    private VideoDisplayModel videoDisplayModel;
    private List<VideoDisplayModel> videoList;
    RecyclerView recyclerView;
    private Context context;
  //  ImageView bluetooth1,  wifi1, share1;

    public YouTubeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment YouTubeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YouTubeFragment newInstance(String param1,String sourceDir,CharSequence labelName,
                                              String packageName1) {
        YouTubeFragment fragment = new YouTubeFragment();
        Bundle args = new Bundle();
        args.putString(YOUTUBE_FULL_LINK, param1);
        args.putString(SOURCE_DIR, sourceDir);
        args.putCharSequence(LABEL_NAME, labelName);
        args.putString(PACKAGE_NAME, packageName1);
        fragment.setArguments(args);
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
            videoid = YoutubeIdConverter.getYoutubeVideoId(mVideo_id);
            mSource_dir = getArguments().getString(SOURCE_DIR);
            mLabel_name = getArguments().getCharSequence(LABEL_NAME);
            mPackage_name = getArguments().getString(PACKAGE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       /* View view = inflater.inflate(R.layout.fragment_you_tube, container, false);
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();*/

        View view = inflater.inflate(R.layout.activity_video_display, container, false);
        /*YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(myContext,
                LinearLayoutManager.VERTICAL, false);

        //Initializing the RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.videoDisplayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RestCall restCall = new RestCall(myContext);
        restCall.getVideoDisplayList(mPackage_name, new CallbackRestResponse() {

            @Override
            public void onResponse(List<VideoDisplayModel> videoResponseModel) {
                videoList = videoResponseModel;
                videoList.add(0, null);
                recyclerView.setAdapter(new AdapterVideoDisplay(myContext,videoList,
                        mSource_dir, mLabel_name, mPackage_name));
            }
            @Override
            public void onError(Throwable throwable) {

            }
        });

        /*View rootView = inflater.inflate(R.layout.activity_app_details, container, false);
        coordinatorLayout=(CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        bluetooth1 = (ImageView) view.findViewById(R.id.material_design_floating_action_bluetooth);
        wifi1 = (ImageView) view.findViewById(R.id.material_design_floating_action_wifi);
        share1 = (ImageView) view.findViewById(R.id.material_design_floating_action_share);

        bluetooth1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),BluetoothPeerList.class);
                intent.putExtra(SOURCE_DIR, mSource_dir);
                intent.putExtra(LABEL_NAME, mLabel_name);
                startActivity(intent);
            }
        });

        wifi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),WiFiPeerList.class);
                intent.putExtra(SOURCE_DIR, mSource_dir);
                intent.putExtra(LABEL_NAME, mLabel_name);
                startActivity(intent);
            }
        });

        share1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareOption();
            }
        });*/

      /*  youTubePlayerFragment.initialize(DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.loadVideo(videoid);
                    youTubePlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });*/

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
      /*  int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){

            bluetooth1 = (ImageView) getView().findViewById(R.id.material_design_floating_action_bluetooth);
            wifi1 = (ImageView) getView().findViewById(R.id.material_design_floating_action_wifi);
            share1 = (ImageView) getView().findViewById(R.id.material_design_floating_action_share);
            bluetooth1.setVisibility(View.GONE);
            wifi1.setVisibility(View.GONE);
            share1.setVisibility(View.GONE);

        }
        else {

            bluetooth1 = (ImageView) getView().findViewById(R.id.material_design_floating_action_bluetooth);
            wifi1 = (ImageView) getView().findViewById(R.id.material_design_floating_action_wifi);
            share1 = (ImageView) getView().findViewById(R.id.material_design_floating_action_share);
            bluetooth1.setVisibility(View.VISIBLE);
            wifi1.setVisibility(View.VISIBLE);
            share1.setVisibility(View.VISIBLE);

        }*/
    }

   /* private void openShareOption() {

        if (Build.VERSION.SDK_INT >= 23) {

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(SOURCE_DIR));
            PackageManager manager = getActivity().getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(sendIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

            for (ResolveInfo resolveInfo : infos) {
                Log.i(TAG, "openShareOption: .....  .... .... ... " + resolveInfo.activityInfo.packageName);
                if (sharePackageSet.contains(resolveInfo.activityInfo.packageName)) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(SOURCE_DIR));
                    intentList.add(new LabeledIntent(intent, resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(manager), resolveInfo.icon));
                }
            }
           *//* Intent intent = new Intent(AppDetailsActivity.this, BluetoothPeerList.class);
            intent.putExtra("APPLICATION", applicationInfo);
            intentList.add(new LabeledIntent(intent,"com.gamecard","Bluethooth",0));*//*

            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            Intent openInChooser = Intent.createChooser(new Intent(), getResources().getString(R.string.share_chooser_text));

            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            try {
                startActivity(openInChooser);
            } catch (Exception exception) {
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_send_app_found), Snackbar.LENGTH_LONG)
                        .show();
            }
        }
        else{

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(SOURCE_DIR));
            startActivity(Intent.createChooser(sendIntent,getResources().getString(R.string.share_chooser_text)));

        }
    }*/
}
package com.gamecard.view;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamecard.R;
import com.gamecard.adapter.BottomSheetAdapter;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.VedioImageLinkModel;
import com.gamecard.utility.DownloadService;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AppDescriptionActivity extends AppCompatActivity {

    private static final String TAG = "AppDetailsActivity";
    protected static boolean downloadStarted = false;
    String[] perms = {Manifest.permission_group.STORAGE};
    int permsRequestCode = 200;
  //  private Context context;
    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    RelativeLayout mainLayout, downloadLayout, mainLayout2;
    PackageManager packageManager;
    CoordinatorLayout coordinatorLayout1;
    List<String> sharePackageSet;
    String sourceDir, loadLabel;
    private GridView bottomSheet;
    private ArrayAdapter<Integer> bottomSheetAdapter;
    private BottomSheetBehavior sheetBehavior;
    private Integer[] bottomItems = {R.drawable.ic_send_black_48dp, R.drawable.ic_photo_library_black_48dp,
    R.drawable.ic_download, R.drawable.ic_person_black_48dp};
    private static int progress;
    private ProgressBar mProgress;
    private TextView showPercentage;
  //  String urlDownload;
    //String extension;
    int id = 1;
    int value;
    String apklink , inputApk, title, inputTitle, inputTitle1, mGameTitle, mIconLink;
    SharedPreferences sharedPreferences;
    ImageView bluetooth1,  wifi1, share1, open1, apkDownload1;
    private int percentProgress4 = 0;

    Realm realm;
    ApplicationInfo applicationInfo;
    ViewPageAdapter viewPageAdapter;
    FloatingActionMenu fab;
    ViewPager viewPager;
    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_description);
        realm=Realm.getInstance(AppDescriptionActivity.this);
        viewPager=(ViewPager)findViewById(R.id.viewpager);

        //applicationInfo = getIntent().getParcelableExtra("APPLICATION");
        sourceDir = getIntent().getStringExtra(VideoFragment.SOURCE_DIR);
        loadLabel = getIntent().getStringExtra(VideoFragment.LABEL_NAME);
        packageName=getIntent().getStringExtra("APPLICATION");

        setUpViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0){
                    fab.hideMenu(false);
                    apkDownload1.setVisibility(ImageView.INVISIBLE);
                }else{
                    fab.showMenu(false);
                    apkDownload1.setVisibility(ImageView.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG,"onPageScrolledStateChanged called");
            }
        });

        View rootView = View.inflate(this,R.layout.activity_app_details,null);
        coordinatorLayout1=(CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        bluetooth1 = (ImageView) findViewById(R.id.material_design_floating_action_bluetooth);
        fab = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        wifi1 = (ImageView) findViewById(R.id.material_design_floating_action_wifi);
        share1 = (ImageView) findViewById(R.id.material_design_floating_action_share);
        open1 = (ImageView) findViewById(R.id.material_design_floating_action_open);

      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View root2 = View.inflate(AppDescriptionActivity.this,R.layout.fragment_image_layout,null);
                ImageView image2 = (ImageView) root2.findViewById(R.id.appImage);
                Animation in = AnimationUtils.loadAnimation(AppDescriptionActivity.this, R.anim.fade_out);
                image2.startAnimation(in);
            }
        });*/

        bluetooth1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppDescriptionActivity.this,BluetoothPeerList.class);
                intent.putExtra(VideoFragment.SOURCE_DIR, sourceDir);
                intent.putExtra(VideoFragment.LABEL_NAME, loadLabel);
                startActivity(intent);
            }
        });

        wifi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppDescriptionActivity.this,WiFiPeerList.class);
                intent.putExtra(VideoFragment.SOURCE_DIR, sourceDir);
                intent.putExtra(VideoFragment.LABEL_NAME, loadLabel);
                startActivity(intent);
            }
        });

        share1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout2 = (RelativeLayout) AppDescriptionActivity.this.findViewById(R.id.layout1);
                mainLayout2.setVisibility(View.GONE);
                //  openShareOption();

                //set bottom sheet(GridView) adapter
                bottomSheetAdapter = new BottomSheetAdapter(AppDescriptionActivity.this,
                        R.layout.item_grid, bottomItems);

                bottomSheet = (GridView) findViewById(R.id.bottom_sheet);
                bottomSheet.setTranslationY(getStatusBarHeight());
                bottomSheet.setAdapter(bottomSheetAdapter);
                sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    boolean first = true;

                    @Override
                    public void onStateChanged(View bottomSheet, int newState) {
                        Log.d("AppDescriptionActivity", "onStateChanged: " + newState);
                    }

                    @Override
                    public void onSlide(View bottomSheet, float slideOffset) {
                        Log.d("AppDescriptionActivity", "onSlide: ");
                        if (first) {
                            first = false;
                            bottomSheet.setTranslationY(0);
                        }
                    }
                });

                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //bottom sheet item click event
                bottomSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        if (position == 0) {
                            Toast.makeText(getBaseContext(), "Send Clicked", Toast.LENGTH_SHORT).show();
                         //   openShareOption();
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody = "Your body here";
                            String shareSub = "Your subject here";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));

                        } else if (position == 1) {
                            Toast.makeText(getBaseContext(), "Search Clicked", Toast.LENGTH_SHORT).show();
                        } else if (position == 2) {
                            Toast.makeText(getBaseContext(), "Download Clicked", Toast.LENGTH_SHORT).show();
                        } else if (position == 3) {
                            Toast.makeText(getBaseContext(), "Contact Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        open1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApp(AppDescriptionActivity.this,packageName);
            }
        });
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

  /*  @Override
    public void onBackPressed() {
        mainLayout2.setVisibility(View.VISIBLE);
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }*/

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean canPerformDownload(){
        return (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission){
        if(canPerformDownload()){
            return(checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    @Override
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

    private void markAsAsked(String permission){
        sharedPreferences.edit().putBoolean(permission, false).apply();
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted){
        ArrayList result = new ArrayList<>();
        for(String perm : wanted){
            if(!hasPermission(perm) && shouldWeAsk(perm)){
                result.add(perm);
            }
        }
        return result;
    }

    private void setUpViewPager(ViewPager viewPager) {
        viewPageAdapter=new ViewPageAdapter(getSupportFragmentManager());

        GameResponseModel realmResults = realm.where(GameResponseModel.class).equalTo("packagename",packageName).findFirst();
        VedioImageLinkModel vedioImageLinkModel=new Gson().fromJson(realmResults.getJsonImageVedioLink(), VedioImageLinkModel.class);
        String vediolink=vedioImageLinkModel.getVedioLink();
        mGameTitle = realmResults.getGametittle();
        mIconLink = realmResults.getIconLink();

        try {
            apklink = vedioImageLinkModel.getApkLink();
            inputApk = apklink.replace(" ", "%20");
            Log.i(TAG, "apklink:.............." + apklink);
            Log.i(TAG, "inputApk:.............." + inputApk);
        }catch (Exception e){
            e.printStackTrace();
        }
        title = realmResults.getGametittle();
        inputTitle = title.replace(" ","_");
        inputTitle1 = inputTitle.trim();

        viewPageAdapter.addUrl(vediolink,vedioImageLinkModel.getImageList());

        viewPager.setAdapter(viewPageAdapter);

        mainLayout = (RelativeLayout) AppDescriptionActivity.this.findViewById(R.id.layout1);
        downloadLayout = (RelativeLayout) AppDescriptionActivity.this.findViewById(R.id.download1);
        apkDownload1 = (ImageView) findViewById(R.id.apkDownload);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        showPercentage = (TextView) findViewById(R.id.percentProgress3);

        if(realmResults.getSuggestion()) {
            mainLayout.setVisibility(View.GONE);
            downloadLayout.setVisibility(View.VISIBLE);

            apkDownload1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnline() && isExternalStorageWritable()) {

                        mProgress.setVisibility(View.VISIBLE);
                        showPercentage.setVisibility(View.VISIBLE);
                        //showPercentage.setText("0 %");
                        mProgress.setProgress(0);

                        // prepare intent which is triggered if the notification is selected
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);

                        try {
                            intent1.setDataAndType(Uri.parse("file://" +
                                    Environment.getExternalStorageDirectory()
                                    + "/GameCenter/" + inputTitle1 + ".apk"),
                                    "application/vnd.android.package-archive");

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        final PendingIntent pIntent = PendingIntent.getActivity(AppDescriptionActivity.this,
                                (int) System.currentTimeMillis(), intent1, 0);

                        Intent intent = new Intent(AppDescriptionActivity.this, DownloadService.class);
                        //* Send optional extras to Download IntentService *//
                        intent.putExtra("url", inputApk);
                        intent.putExtra("title", inputTitle1);
                        intent.putExtra("receiver", new ResultReceiver(new Handler()) {
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                super.onReceiveResult(resultCode, resultData);
                                switch (resultCode) {
                                    case DownloadService.STATUS_RUNNING:
                                        AppDescriptionActivity.downloadStarted = true;

                                        mNotifyManager = (NotificationManager)
                                                getSystemService(Context.NOTIFICATION_SERVICE);
                                        mBuilder = new NotificationCompat.Builder(AppDescriptionActivity.this);
                                        mBuilder.setContentTitle("Download")
                                                .setContentText("Download in progress")
                                                .setOngoing(true)
                                                .setProgress(100,0,false)
                                                .setSmallIcon(R.drawable.ic_download)
                                                .setContentIntent(pIntent)
                                                .setAutoCancel(true);

                                        // Issues the notification
                                        mNotifyManager.notify(id, mBuilder.build());
                                        Toast.makeText(AppDescriptionActivity.this,
                                                "Download in PROGRESS...", Toast.LENGTH_LONG).show();
                                        break;
                                    case DownloadService.STATUS_FINISHED:
                                        //* Hide progress & extract result from bundle *//*
                                        mBuilder.addAction(0, " \t\t\t\t\t\t\t\t\t\t\t\t\t\t Install", pIntent);
                                        Toast.makeText(AppDescriptionActivity.this, "Download Complete",
                                                Toast.LENGTH_LONG).show();

                                        mBuilder.setContentText("Download Complete");
                                        // Removes the progress bar
                                        mBuilder.setProgress(0, 0, false);
                                        mNotifyManager.notify(id, mBuilder.build());

                                        String results = resultData.getString("result");
                                        //* Update with result *//*
                                        Toast.makeText(AppDescriptionActivity.this, results ,
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    case DownloadService.STATUS_ERROR:
                                        //* Handle the error *//*
                                        String error = resultData.getString(Intent.EXTRA_TEXT);
                                        Toast.makeText(AppDescriptionActivity.this, error,
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    case DownloadService.UPDATE_PROGRESS:

                                        mBuilder.setProgress(100,progress,false);
                                        mNotifyManager.notify(id, mBuilder.build());

                                        progress = resultData.getInt("progress");
                                        mProgress.setProgress(progress);

                                        if(progress < 100){
                                            percentProgress4 = percentProgress4 + 1;    // update progress
                                            postProgress(percentProgress4);

                                        }
                                        if (progress == 100) {
                                            try {
                                                mProgress.setVisibility(View.INVISIBLE);
                                                showPercentage.setVisibility(View.INVISIBLE);

                                                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                                // view apk file
                                                intent1.setDataAndType(Uri.parse("file://" +
                                                        Environment.getExternalStorageDirectory()
                                                    + "/GameCenter/" + inputTitle1 + ".apk"),
                                                        "application/vnd.android.package-archive");

                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent1);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                }
                            }
                        });

                        try {
                            if (Build.VERSION.SDK_INT >= 23) {
                                requestPermissions(perms, permsRequestCode);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startService(intent);
                        if(downloadStarted) {
                            Toast.makeText(AppDescriptionActivity.this, "PLEASE WAIT...",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AppDescriptionActivity.this, "You are Offline",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            mainLayout.setVisibility(View.VISIBLE);
            downloadLayout.setVisibility(View.GONE);
        }
    }

    private void postProgress(int percentProgress4) {
        percentProgress4 = mProgress.getProgress();
        String strProgress = String.valueOf(percentProgress4) + " %";
        showPercentage.setText(strProgress);
    }

    class  ViewPageAdapter extends FragmentPagerAdapter {
        private List<String> urlList = new ArrayList<>();
        String vedioLink;
        boolean vedioPresent=false;
        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

           /* if(vedioLink == ""){
                if(position == 0) {
                    return ImageFragment.newInstance(urlList.get(position));
                } else {
                    return VideoFragment.newInstance(vedioLink, urlList, sourceDir,loadLabel,
                            packageName, mGameTitle, mIconLink);
                }
            }
            if(vedioPresent){*/
                if(position==0){
                    return VideoFragment.newInstance(vedioLink, urlList, sourceDir,loadLabel,
                            packageName, mGameTitle, mIconLink);
                }
          /*  }
            if(vedioLink != null) {*/
                return ImageFragment.newInstance(urlList.get(position));
           /* }
            return null;*/
        }

        @Override
        public int getCount() {
            viewPager.setOffscreenPageLimit(urlList.size());
            return urlList.size();
        }

        public void addUrl(String vedioLink,List<String> urlList){
            this.urlList=urlList;
            this.vedioLink=vedioLink;

            if(vedioLink != ""){
                vedioPresent=true;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

    public static boolean openApp(Context context, String packageName1) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName1);
            if (i == null) {
                return false;
                //throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void openShareOption() {

        if (Build.VERSION.SDK_INT >= 23) {

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(VideoFragment.SOURCE_DIR));
            PackageManager manager = getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(sendIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

            for (ResolveInfo resolveInfo : infos) {
                Log.i(TAG, "openShareOption: .....  .... .... ... " + resolveInfo.activityInfo.packageName);
                if (sharePackageSet.contains(resolveInfo.activityInfo.packageName)) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(VideoFragment.SOURCE_DIR));
                    intentList.add(new LabeledIntent(intent, resolveInfo.activityInfo.packageName,
                            resolveInfo.loadLabel(manager), resolveInfo.icon));
                }
            }
           /* Intent intent = new Intent(AppDetailsActivity.this, BluetoothPeerList.class);
            intent.putExtra("APPLICATION", applicationInfo);
            intentList.add(new LabeledIntent(intent,"com.gamecard","Bluethooth",0));*/

            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            Intent openInChooser = Intent.createChooser(new Intent(), getResources().getString(R.string.share_chooser_text));

            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            try {
                startActivity(openInChooser);
            } catch (Exception exception) {
                Snackbar.make(coordinatorLayout1, getResources().getString(R.string.no_send_app_found), Snackbar.LENGTH_LONG)
                        .show();
            }
        }
        else{

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(VideoFragment.SOURCE_DIR));
            startActivity(Intent.createChooser(sendIntent,getResources().getString(R.string.share_chooser_text)));

        }
    }
}
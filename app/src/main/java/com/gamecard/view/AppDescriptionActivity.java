package com.gamecard.view;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.gamecard.R;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.VedioImageLinkModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AppDescriptionActivity extends AppCompatActivity {

    private static final String TAG = "AppDetailsActivity";
    PackageManager packageManager;
    CoordinatorLayout coordinatorLayout1;
    List<String> sharePackageSet;
    String sourceDir, loadLabel;
    ImageView bluetooth1,  wifi1, share1;

    Realm realm;
    ApplicationInfo applicationInfo;
    ViewPageAdapter viewPageAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_description);
        realm=Realm.getInstance(AppDescriptionActivity.this);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        applicationInfo = getIntent().getParcelableExtra("APPLICATION");
        sourceDir = getIntent().getStringExtra(YouTubeFragment.SOURCE_DIR);
        loadLabel = getIntent().getStringExtra(YouTubeFragment.LABEL_NAME);
        setUpViewPager(viewPager);

        View rootView = View.inflate(this,R.layout.activity_app_details,null);
        coordinatorLayout1=(CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        bluetooth1 = (ImageView) findViewById(R.id.material_design_floating_action_bluetooth);
        wifi1 = (ImageView) findViewById(R.id.material_design_floating_action_wifi);
        share1 = (ImageView) findViewById(R.id.material_design_floating_action_share);

        bluetooth1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppDescriptionActivity.this,BluetoothPeerList.class);
                intent.putExtra(YouTubeFragment.SOURCE_DIR, sourceDir);
                intent.putExtra(YouTubeFragment.LABEL_NAME, loadLabel);
                startActivity(intent);
            }
        });

        wifi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppDescriptionActivity.this,WiFiPeerList.class);
                intent.putExtra(YouTubeFragment.SOURCE_DIR, sourceDir);
                intent.putExtra(YouTubeFragment.LABEL_NAME, loadLabel);
                startActivity(intent);
            }
        });

        share1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareOption();
            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        viewPageAdapter=new ViewPageAdapter(getSupportFragmentManager());
        GameResponseModel realmResults = realm.where(GameResponseModel.class).equalTo("packagename",applicationInfo.packageName).findFirst();
        VedioImageLinkModel vedioImageLinkModel=new Gson().fromJson(realmResults.getJsonImageVedioLink(), VedioImageLinkModel.class);
        String vediolink=vedioImageLinkModel.getVedioLink();

        viewPageAdapter.addUrl(vediolink,vedioImageLinkModel.getImageList());

        viewPager.setAdapter(viewPageAdapter);
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
            if(vedioPresent){
                if(position==0){
                    return YouTubeFragment.newInstance(vedioLink,sourceDir,loadLabel);
                }
            }
            return ImageFragment.newInstance(urlList.get(position));

        }

        @Override
        public int getCount() {
            viewPager.setOffscreenPageLimit(urlList.size());
            return urlList.size();
        }

        public void addUrl(String vedioLink,List<String> urlList){
            this.urlList=urlList;
            this.vedioLink=vedioLink;

            if(!vedioLink.equals("")){
                vedioPresent=true;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void openShareOption() {

        if (Build.VERSION.SDK_INT >= 23) {

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(YouTubeFragment.SOURCE_DIR));
            PackageManager manager = getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(sendIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

            for (ResolveInfo resolveInfo : infos) {
                Log.i(TAG, "openShareOption: .....  .... .... ... " + resolveInfo.activityInfo.packageName);
                if (sharePackageSet.contains(resolveInfo.activityInfo.packageName)) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(YouTubeFragment.SOURCE_DIR));
                    intentList.add(new LabeledIntent(intent, resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(manager), resolveInfo.icon));
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
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(YouTubeFragment.SOURCE_DIR));
            startActivity(Intent.createChooser(sendIntent,getResources().getString(R.string.share_chooser_text)));

        }
    }
}

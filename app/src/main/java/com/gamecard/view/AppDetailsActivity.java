package com.gamecard.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamecard.R;
import com.gamecard.utility.AnimationUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AppDetailsActivity";
    PackageManager packageManager;
    ApplicationInfo applicationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView cardView = (CardView) findViewById(R.id.card_view);
        AnimationUtility.animateUp(cardView, 1600);

        packageManager = getPackageManager();
        applicationInfo = getIntent().getParcelableExtra("APPLICATION");

        getSupportActionBar().setTitle(applicationInfo.loadLabel(packageManager));

        TextView appName = (TextView) findViewById(R.id.appName);
        TextView companyName = (TextView) findViewById(R.id.companyName);
        ImageView imageIcon = (ImageView) findViewById(R.id.imageIcon);
        TextView gameDescription = (TextView) findViewById(R.id.gameDescription);
        imageIcon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        appName.setText(applicationInfo.loadLabel(packageManager));
        companyName.setText(applicationInfo.packageName);
        gameDescription.setText(applicationInfo.loadDescription(packageManager));

    }

    //Share button click event
    public void shareImageClick(View view) {
        showMenu(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.store:
                return true;
            case R.id.wifi:
                openPeerListActivity();
                return true;
            case R.id.bluetooth:
                openBluetoothPeerList();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMenu(View view) {
        PopupMenu menu=new PopupMenu(AppDetailsActivity.this,view);
        menu.getMenuInflater().inflate(R.menu.share_menu,menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bluetooth:
                        openBluetoothPeerList();
                        return true;
                    case R.id.wifi:
                        openPeerListActivity();
                        return true;

                    case R.id.share:
                        openShareOption();
                        return true;

                    default:
                        return true;
                }
            }
        });
        menu.show();
    }

    private void openShareOption() {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(applicationInfo.sourceDir));
            PackageManager manager = getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(sendIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

            for (ResolveInfo resolveInfo : infos) {
                Log.i(TAG, "openShareOption: .....  .... .... ... " + resolveInfo.activityInfo.packageName);
                if (sharePackageSet.contains(resolveInfo.activityInfo.packageName)) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(applicationInfo.sourceDir));
                    intentList.add(new LabeledIntent(intent, resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(getPackageManager()), resolveInfo.icon));
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
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_send_app_found), Snackbar.LENGTH_LONG)
                        .show();
            }
        }
        else{
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.android.package-archive");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(applicationInfo.sourceDir));
            startActivity(Intent.createChooser(sendIntent,getResources().getString(R.string.share_chooser_text)));
        }
    }


    private void openPeerListActivity() {
        Intent intent = new Intent(AppDetailsActivity.this, WiFiPeerList.class);
        intent.putExtra("APPLICATION", applicationInfo);
        startActivity(intent);
    }

    private void openBluetoothPeerList() {
        Intent intent = new Intent(AppDetailsActivity.this, BluetoothPeerList.class);
        intent.putExtra("APPLICATION", applicationInfo);
        startActivity(intent);
    }

    public void openBluetooth(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setPackage("com.android.bluetooth");
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(applicationInfo.sourceDir)));
        try{
            startActivity(intent);
        }catch (Exception exception){
            Log.e(TAG, "openBluetooth: ", exception);
        }
    }
}

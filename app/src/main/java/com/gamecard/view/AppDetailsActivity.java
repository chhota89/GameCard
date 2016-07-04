package com.gamecard.view;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.gamecard.R;
import com.gamecard.utility.AnimationUtility;

import java.io.File;

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
                openBluetooth();
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
                        openBluetooth();
                        return true;
                    case R.id.wifi:
                        openPeerListActivity();
                        return true;
                    default:
                        return true;
                }
            }
        });
        menu.show();
    }

    private void openPeerListActivity() {
        Intent intent = new Intent(AppDetailsActivity.this, PeerList.class);
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

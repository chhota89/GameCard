package com.gamecard.view;

import android.content.pm.ApplicationInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gamecard.R;
import com.gamecard.model.GameResponseModel;
import com.gamecard.model.VedioImageLinkModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class AppDescriptionActivity extends AppCompatActivity {

    Realm realm;
    ApplicationInfo applicationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_description);
        realm=Realm.getInstance(AppDescriptionActivity.this);
        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);
        applicationInfo = getIntent().getParcelableExtra("APPLICATION");
        setUpViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPageAdapter viewPageAdapter=new ViewPageAdapter(getSupportFragmentManager());
        GameResponseModel realmResults = realm.where(GameResponseModel.class).equalTo("packagename",applicationInfo.packageName).findFirst();
        VedioImageLinkModel vedioImageLinkModel=new Gson().fromJson(realmResults.getJsonImageVedioLink(), VedioImageLinkModel.class);
        viewPageAdapter.addUrl(vedioImageLinkModel.getImageList());
        String vediolink=vedioImageLinkModel.getVedioLink();
        viewPager.setAdapter(viewPageAdapter);
    }

    class  ViewPageAdapter extends FragmentPagerAdapter {
        private List<String> urlList = new ArrayList<>();

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(urlList.get(position));
        }

        @Override
        public int getCount() {
            return urlList.size();
        }

        public void addUrl(List<String> urlList){
            this.urlList=urlList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}

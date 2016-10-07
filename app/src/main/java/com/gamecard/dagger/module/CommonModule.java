package com.gamecard.dagger.module;

import android.app.Application;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.gamecard.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bridgeit on 7/10/16.
 */

@Module
public class CommonModule {

    @Provides
    ProgressDialog provideProgressDialog(Application application){
        ProgressDialog progressDialog=new ProgressDialog(application);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    @Singleton
    @Provides
    NotificationManager provideNotificationManager(Application application){
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Singleton
    @Provides
    NotificationCompat.Builder provideNotificationCompatBuilder(Application application){
        Bitmap largeIcon = BitmapFactory.decodeResource(application.getResources(), R.mipmap.ic_launcher_game_center);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_game_center);
        mBuilder.setLargeIcon(largeIcon);
        return mBuilder;
    }
}

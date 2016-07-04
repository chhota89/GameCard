package com.gamecard.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bridgeit on 4/7/16.
 */

public class EditSharedPrefrence {

    private static String USER_PREF="UserPref";
    private static String USER_LOGIN="USER_LOGIN";
    private static String USER_NAME="USER_NAME";
    private static String PROFILE_PIC="PROFILE_PIC";

    public static void setUserLogin(boolean flag, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_PREF, context.MODE_PRIVATE).edit();
        editor.putBoolean(USER_LOGIN,flag);
        editor.commit();
    }

    public static boolean getUserLogin(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREF, context.MODE_PRIVATE);
        return prefs.getBoolean(USER_LOGIN, false);
    }

    public static void setUserName(String name, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_NAME, context.MODE_PRIVATE).edit();
        editor.putString(USER_NAME,name);
        editor.commit();
    }

    public static String getUserName(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_NAME, context.MODE_PRIVATE);
        return prefs.getString(USER_NAME, null);
    }

    public static void setProfilePic(String URI, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_PIC, context.MODE_PRIVATE).edit();
        editor.putString(USER_NAME,URI);
        editor.commit();
    }

    public static String getProfilePic(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PROFILE_PIC, context.MODE_PRIVATE);
        return prefs.getString(USER_NAME, null);
    }
}

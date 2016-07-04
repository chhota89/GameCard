package com.gamecard.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bridgeit on 29/6/16.
 */

public class ApplicationUtility {

    public static List<Object> getInstallApp(List<ApplicationInfo> allApp) {
        List<Object> installAppList = new ArrayList<>();
        for (ApplicationInfo applicationInfo : allApp) {
            if (!isSystemPackage(applicationInfo)) {
                installAppList.add(applicationInfo);
            }
        }
        return installAppList;
    }

    public static List<ApplicationInfo> getAllPackages(Context context) {
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        return pm.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    private static boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

}

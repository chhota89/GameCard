package com.gamecard.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bridgeit on 29/6/16.
 */

public class ApplicationUtility {

    public static Map<String,ApplicationInfo> getInstallApp(List<ApplicationInfo> allApp) {
        Map<String,ApplicationInfo> applicationInfoMap=new HashMap<>();
        for (ApplicationInfo applicationInfo : allApp) {
            if (!isSystemPackage(applicationInfo)) {
                applicationInfoMap.put(applicationInfo.packageName,applicationInfo);
            }
        }
        return applicationInfoMap;
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

package com.gamecard.utility;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by bridgeit on 8/7/16.
 */

public class FileUtility {

    private static final String TAG=FileUtility.class.getSimpleName();

    public static void copyFile(InputStream inputstream, FileOutputStream fileOutputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        InputStreamReader isr = new InputStreamReader(inputstream);
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        while (true) {
            bytesRead = inputstream.read(buffer, 0, buffer.length);
            if (bytesRead == -1) {
                break;
            }
            bos.write(buffer, 0, bytesRead);
            bos.flush();

        }
        bos.close();
    }

    public static boolean checkSpaceInSDcard(){
        long avilableSpace=getSpaceInSDcard();
        Log.i(TAG, "checkSpaceInSDcard: Avilable space is .... :"+avilableSpace);
        if(avilableSpace>80)
            return true;
        else
            return false;
    }

    private static long getSpaceInSDcard(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = getBlockSize(stat) * getBlockCount(stat);
        return bytesAvailable / 1048576;
    }

    private static long getBlockSize(StatFs statFs){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
            // Do something for JELLY_BEAN_MR2 and above versions
            return statFs.getBlockSizeLong();
        } else{
            // do something for phones running an SDK before JELLY_BEAN_MR2
            return statFs.getBlockSize();
        }
    }

    private static long getBlockCount(StatFs statFs){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
            // Do something for JELLY_BEAN_MR2 and above versions
            return statFs.getBlockCountLong();
        } else{
            // do something for phones running an SDK before JELLY_BEAN_MR2
            return statFs.getBlockCount();
        }
    }

}

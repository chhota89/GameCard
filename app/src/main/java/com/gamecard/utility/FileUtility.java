package com.gamecard.utility;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by bridgeit on 8/7/16.
 */

public class FileUtility {

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

}

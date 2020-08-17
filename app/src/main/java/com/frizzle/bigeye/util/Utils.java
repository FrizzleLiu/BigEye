package com.frizzle.bigeye.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * author: LWJ
 * date: 2020/8/10$
 * description
 */
public class Utils {
    public static void copyAssets(Context context, String src, String dst) {
        File file = new File(dst);
        if (file.exists()) {
            file.delete();
        }
        try {
            InputStream is = context.getAssets().open(src);
            FileOutputStream fos = new FileOutputStream(file);
            int len;
            byte[] b = new byte[2048];
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.way.heard.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by pc on 2016/4/21.
 */
public class FileUtil {

    public static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getDrawableBytes(Context ctx, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), resId);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output);
        byte[] bytes = output.toByteArray();
        return bytes;
    }

    private File createCacheFile(Context ctx, String filename,String content) throws IOException {
        File tmpFile = new File(ctx.getCacheDir(), filename);
        byte[] bytes = content.getBytes();
        FileOutputStream outputStream = new FileOutputStream(tmpFile);
        outputStream.write(bytes, 0, bytes.length);
        outputStream.close();
        return tmpFile;
    }
}

package com.ufo.lottery.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import java.io.IOException;
import java.io.InputStream;

public class BmFactory {
    private static SoftHashMap<String, Bitmap> static_factory = new SoftHashMap<>();
    private static Context static_context;


    public static void init(Context context) {
        static_context = context;
    }

    public static int dip2px(float dipValue) {
        final float scale = static_context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public static Bitmap readBitMap(int resId, boolean isHd) {
        Options opt = new Options();
        if (isHd) {
            opt.inPreferredConfig = Config.ARGB_8888;
        } else {
            opt.inPreferredConfig = Config.RGB_565;
        }

        // 获取资源图片
        InputStream is = static_context.getResources().openRawResource(resId);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap getBitmapResource(int id, boolean cache, boolean isHd) {
        String key = id + "stat";
        Bitmap bm = static_factory.get(key);
        if (bm == null || bm.isRecycled()) {
            try {
                bm = readBitMap(id, isHd);
            } catch (OutOfMemoryError e) {
                System.gc();
                bm = readBitMap(id, isHd);
            }
            if (cache) {
                static_factory.put(key, bm);
            }
        }
        return bm;
    }


    public static Bitmap getBitmapResource(int id) {
        return getBitmapResource(id, true, true);
    }


}
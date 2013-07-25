package com.missionhub.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.missionhub.application.Application;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

    private static LruBitmapCache mInstance;

    private LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public static LruBitmapCache getInstance() {
        if (mInstance == null) {
            int cacheSize = 1024 * 1024 * 4;
            try {
                int memClass = ((ActivityManager) Application.getContext().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
                cacheSize = 1024 * 1024 * memClass / 8;
            } catch (Exception e) {
                Log.e("LruBitmapCache", e.getMessage(), e);
            /* ignore */
            }
            synchronized (LruBitmapCache.class) {
                mInstance = new LruBitmapCache(cacheSize);
            }
        }
        return mInstance;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return getInstance().get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        getInstance().put(url, bitmap);
    }

}
package com.example.rogerzzzz.cityrecall.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by rogerzzzz on 16/3/30.
 */
public class ImageCacheUtils implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(5 * 1024);

    @Override
    public Bitmap getBitmap(String url) {
        if (url == null || url.equals("")) {
            return null;
        }
        return imageCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (url == null || bitmap == null) {
            return;
        }
        if (getBitmap(url) == null) {
            imageCache.put(url, bitmap);
        }
    }

}

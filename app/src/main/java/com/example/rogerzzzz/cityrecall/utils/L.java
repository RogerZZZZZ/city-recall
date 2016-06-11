package com.example.rogerzzzz.cityrecall.utils;

import android.util.Log;

/**
 * Created by rogerzzzz on 16/5/11.
 */
public class L {
    private static final boolean isDebug = true;

    public static void d(String debugContent){
        if(isDebug){
            Log.d("debug", debugContent);
        }
    }

    public static void e(String debugContent){
        if(isDebug){
            Log.e("debug_error", debugContent);
        }
    }
}

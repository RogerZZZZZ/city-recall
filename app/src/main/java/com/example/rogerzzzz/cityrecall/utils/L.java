package com.example.rogerzzzz.cityrecall.utils;

import android.util.Log;

/**
 * Created by rogerzzzz on 16/4/18.
 */
//方便调试
public class L {
    private static final boolean isDebug = true;

    public static void i(String debugContent){
        if(isDebug){
            Log.i("Debug content", debugContent);
        }
    }

    public static void d(String debugContent){
        if(isDebug){
            Log.d("Debug content", debugContent);
        }
    }

    public static void e(String debugContent){
        if(isDebug){
            Log.e("Debug content", debugContent);
        }
    }

    public static void v(String debugContent){
        if(isDebug){
            Log.v("Debug content", debugContent);
        }
    }
}

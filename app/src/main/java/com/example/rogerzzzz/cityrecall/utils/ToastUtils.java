package com.example.rogerzzzz.cityrecall.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by rogerzzzz on 16/3/18.
 */
public class ToastUtils {
    private static Toast toast;

    public static void showToast(Context context, CharSequence text, int duration){
        if(toast == null){
            toast = Toast.makeText(context, text, duration);
        }else{
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }
}

package com.example.rogerzzzz.cityrecall.widget;

import android.media.MediaPlayer;
import android.util.Log;

import com.example.rogerzzzz.cityrecall.enity.IVoiceManager;

/**
 * Created by rogerzzzz on 16/5/16.
 */
public class UPlayer implements IVoiceManager {

    private final String TAG = UPlayer.class.getName();
    private String path;

    private MediaPlayer mPlayer;

    public UPlayer(String path) {
        this.path = path;
    }

    @Override
    public boolean start() {
        mPlayer = new MediaPlayer();
        try {
            //设置要播放的文件
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            //播放
            mPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
        }

        return false;
    }

    @Override
    public boolean stop() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        return false;
    }
}

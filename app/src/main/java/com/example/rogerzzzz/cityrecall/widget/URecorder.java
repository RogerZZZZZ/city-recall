package com.example.rogerzzzz.cityrecall.widget;

import android.media.MediaRecorder;
import android.util.Log;

import com.example.rogerzzzz.cityrecall.enity.IVoiceManager;

import java.io.IOException;

/**
 * Created by rogerzzzz on 16/5/16.
 */
public class URecorder implements IVoiceManager {
    private final String TAG = URecorder.class.getName();
    private String        path;
    private MediaRecorder mRecorder;
    public URecorder(String path){
        this.path = path;
    }

    /*
     * 开始录音
     * @return boolean
     */
    @Override
    public boolean start() {
        mRecorder = new MediaRecorder();
        //设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(path);
        //设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        //录音
        mRecorder.start();
        return false;
    }

    /*
     * 停止录音
     * @return boolean
     */
    @Override
    public boolean stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return false;
    }
}

package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.example.rogerzzzz.cityrecall.utils.L;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.UPlayer;
import com.example.rogerzzzz.cityrecall.widget.URecorder;

import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/5/16.
 */
public class HotStatusActivity extends AppCompatActivity {
    @Bind(R.id.startRecording)
    Button startBtn;

    @Bind(R.id.stopRecording)
    Button stopRecordingBtn;

    @Bind(R.id.playing)
    Button playBtn;

    @Bind(R.id.stopPlaying)
    Button stopPlayingBtn;

    @Bind(R.id.uploadRecord)
    Button uploadBtn;

    @Bind(R.id.playuploadRecord)
    Button playUploadBtn;

    private String path = null;
    private URecorder recorder;
    private UPlayer   player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotstatus);
        ButterKnife.bind(this);

        UserUtils.initCloudService(this);

        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/ione.pcm";
        L.d(path);

        recorder = new URecorder(path);
        player = new UPlayer(path);

        //开始录音
        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(HotStatusActivity.this, "start record", Toast.LENGTH_SHORT).show();

                recorder.start();
            }
        });

        //停止录音
        stopRecordingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(HotStatusActivity.this, "stop record", Toast.LENGTH_SHORT).show();
                recorder.stop();
            }
        });

        //开始播放
       playBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(HotStatusActivity.this, "start play", Toast.LENGTH_SHORT).show();
                player.start();
            }
        });

        //停止播放
        stopPlayingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(HotStatusActivity.this, "stop play", Toast.LENGTH_SHORT).show();
                player.stop();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    final AVFile file = AVFile.withAbsoluteLocalPath("record", path);
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null){
//                                file.getUrl();
                                L.d("url:"+file.getUrl());
                            }else{
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        playUploadBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                AVFile file = new AVFile();
                UPlayer player = new UPlayer("http://ac-vbq8hem7.clouddn.com/Gt40VxkWe42qCVX2VVIKTevYnVl8J5NBNgFm5gPE");
                player.start();
            }
        });
    }
}

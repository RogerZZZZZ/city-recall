package com.example.rogerzzzz.cityrecall;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rogerzzzz.cityrecall.widget.StartPointSeekbar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/5/13.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.scape_seekbar)
    StartPointSeekbar scapeSeekBar;

    @Bind(R.id.total_seekbar)
    StartPointSeekbar totalSeekbar;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.scape_num)
    TextView scapeNum_tv;

    @Bind(R.id.total_num)
    TextView totalNum_tv;

    @Bind(R.id.makesureBtn)
    Button makesureBtn;

    @Bind(R.id.rootLayout)
    LinearLayout rootLayout;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int seachScape;
    private int seachNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolbar();
        initSetting();
        initSeekBar();

        makesureBtn.setOnClickListener(this);

        scapeSeekBar.setOnSeekBarChangeListener(new StartPointSeekbar.OnSeekBarChangeListener() {
            @Override
            public void onOnSeekBarValueChange(StartPointSeekbar bar, double value) {
                seachScape = (int) Math.floor(value);
                scapeNum_tv.setText(seachScape + "m");
            }
        });

        totalSeekbar.setOnSeekBarChangeListener(new StartPointSeekbar.OnSeekBarChangeListener() {
            @Override
            public void onOnSeekBarValueChange(StartPointSeekbar bar, double value) {
                seachNum = (int) Math.floor(value);
                totalNum_tv.setText(seachNum + "个");
            }
        });

    }

    private void initSetting(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        seachScape = sharedPreferences.getInt("seachScape", 3000);
        seachNum = sharedPreferences.getInt("seachNum", 10);
    }

    private void initToolbar(){
        toolbar.setTitle("设置页面");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initSeekBar(){
        scapeSeekBar.setProgress(seachScape);
        totalSeekbar.setProgress(seachNum);
        scapeNum_tv.setText(Math.floor(seachScape) + "m");
        totalNum_tv.setText(Math.floor(seachNum) + "个");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.makesureBtn:
                editor.putInt("seachScape", seachScape);
                editor.putInt("seachNum", seachNum);
                editor.commit();
                Snackbar.make(rootLayout, "成功设置", Snackbar.LENGTH_LONG).show();
                break;
        }
    }
}

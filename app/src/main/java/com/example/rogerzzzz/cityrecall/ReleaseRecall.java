package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rogerzzzz.cityrecall.adapter.EmotionPagerAdapter;
import com.example.rogerzzzz.cityrecall.adapter.WriteStatusGridImgsAdapter;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.widget.WrapHeightGridView;

import java.util.ArrayList;

/**
 * Created by rogerzzzz on 16/3/17.
 */
public class ReleaseRecall extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private EditText et_write_status;
    private WrapHeightGridView gv_write_status;
    private View include_retweeted_status_card;
    private ImageView iv_rstatus_img;
    private TextView tv_rstatus_username;
    private TextView tv_rstatus_content;

    private ImageView iv_image;
    private ImageView iv_emoji;
    private ImageView iv_add;

    private LinearLayout ll_emotion_dashboard;
    private ViewPager vp_emotion_dashboard;

    private ProgressDialog progressDialog;

    private WriteStatusGridImgsAdapter statusGridImgsAdapter;
    private ArrayList<Uri> imgUri = new ArrayList<Uri>();
    private EmotionPagerAdapter emotionPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.release_recall);

        initView();
    }

    private void initView(){
        //标题栏
        new TitleBuilder(this)
                .setTitleText("发微博")
                .setLeftText("取消")
                .setLeftOnClickListener(this)
                .setRightText("发送")
                .setRightOnClickListener(this)
                .build();

        //输入框
        et_write_status = (EditText) findViewById(R.id.et_write_status);
        //添加的九宫格图片
        gv_write_status = (WrapHeightGridView) findViewById(R.id.gv_write_status);
        //底部添加栏
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
        //表情选择面板
        ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
        vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);
        //进度条
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发送中..");

        statusGridImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUri, gv_write_status);
        gv_write_status.setAdapter(statusGridImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        iv_image.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);

        

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}

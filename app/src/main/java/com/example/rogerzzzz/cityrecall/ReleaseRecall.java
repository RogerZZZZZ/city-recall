package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.EmotionGvAdapter;
import com.example.rogerzzzz.cityrecall.adapter.EmotionPagerAdapter;
import com.example.rogerzzzz.cityrecall.adapter.WriteStatusGridImgsAdapter;
import com.example.rogerzzzz.cityrecall.enity.Emotion;
import com.example.rogerzzzz.cityrecall.utils.DialogUtils;
import com.example.rogerzzzz.cityrecall.utils.DisplayUtils;
import com.example.rogerzzzz.cityrecall.utils.ImageUtils;
import com.example.rogerzzzz.cityrecall.utils.StringUtils;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.WrapHeightGridView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rogerzzzz on 16/3/17.
 */
public class ReleaseRecall extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private EditText et_write_status;
    private WrapHeightGridView gv_write_status;

    private ImageView iv_image;
    private ImageView iv_emoji;
    private ImageView iv_add;
    private TextView titlebar_tv_left;
    private TextView titlebar_tv_right;

    private LinearLayout ll_emotion_dashboard;
    private ViewPager vp_emotion_dashboard;

    private ProgressDialog progressDialog;

    private WriteStatusGridImgsAdapter statusGridImgsAdapter;
    private ArrayList<Uri> imgUri = new ArrayList<Uri>();
    private EmotionPagerAdapter emotionPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_recall);

        initView();
        initEmotion();
    }

    private void initView(){
        //标题栏
        new TitleBuilder(this)
                .setTitleText("新动态")
                .setLeftText("取消")
                .setLeftOnClickListener(this)
                .setRightText("发送")
                .setRightOnClickListener(this)
                .build();
        //顶部
        titlebar_tv_left = (TextView) findViewById(R.id.titlebar_tv_left);
        titlebar_tv_right = (TextView) findViewById(R.id.titlebar_tv_right);
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

        statusGridImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUri, gv_write_status, ReleaseRecall.this);
        gv_write_status.setAdapter(statusGridImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        titlebar_tv_left.setOnClickListener(this);
        titlebar_tv_right.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
    }

    //ToDo 发送逻辑
    private void sendStatus() throws FileNotFoundException, AVException {
        String comment = et_write_status.getText().toString();
        if(TextUtils.isEmpty(comment)){
            ToastUtils.showToast(this, "发送内容不能为空", Toast.LENGTH_SHORT);
            return;
        }

        new Thread(){
            @Override
            public void run() {
                super.run();
                UserUtils.initCloudService(ReleaseRecall.this);
                try {
                    ArrayList<String> arr = UserUtils.savePic(imgUri, ReleaseRecall.this);
                    AVQuery<AVObject> query = new AVQuery<AVObject>("_File");
                    query.whereContainedIn("name", arr);
                    query.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> avObjects, AVException e) {
                            Log.d("成功", avObjects.size() + "");
                            if(e == null && avObjects.size() > 0){
                                String urlString = StringUtils.arrayListToString(avObjects);
                                Log.d("成功", urlString + "");
                            } else if(e != null){
                                Log.d("失败", e.getMessage());
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (AVException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //初始化表情面板内容
    private void initEmotion(){
        int gvWidth = DisplayUtils.getScreenWidthPixels(this);
        int spacing = DisplayUtils.dp2px(this, 8);
        //GridView 中表情的宽度
        int itemWidth = (gvWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        //遍历
        for(String emojiName : Emotion.emojiMap.keySet()){
            emotionNames.add(emojiName);
            if(emotionNames.size() == 20){
                GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);
                emotionNames = new ArrayList<String>();
            }
        }

        if(emotionNames.size() > 0){
            GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        emotionPagerAdapter = new EmotionPagerAdapter(gvs);
        vp_emotion_dashboard.setAdapter(emotionPagerAdapter);
        //外面包裹一层layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);

    }

    private void updateImgs(){
        if(imgUri.size() > 0){
            gv_write_status.setVisibility(View.VISIBLE);
            statusGridImgsAdapter.notifyDataSetChanged();
        }else{
            gv_write_status.setVisibility(View.GONE);
        }
    }

    //创建显示表情的GridView
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight){
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);
        LayoutParams params = new LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return gv;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.titlebar_tv_left:
                finish();
                break;
            case R.id.titlebar_tv_right:
                ToastUtils.showToast(ReleaseRecall.this, "title_bar_right", Toast.LENGTH_SHORT);
                try {
                    sendStatus();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (AVException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_image:
                DialogUtils.showImagePickDialog(this);
                break;
            case R.id.iv_emoji:
                if(ll_emotion_dashboard.getVisibility() == View.VISIBLE){
                    iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
                    ll_emotion_dashboard.setVisibility(View.GONE);
                }else{
                    iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_add:
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object itemAdapter = adapterView.getAdapter();

        if(itemAdapter instanceof WriteStatusGridImgsAdapter) {
            if(position == statusGridImgsAdapter.getCount() - 1){
                DialogUtils.showImagePickDialog(this);
            }
        }else if(itemAdapter instanceof EmotionGvAdapter){
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;
            if(position == emotionGvAdapter.getCount() - 1){
                et_write_status.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                String emotionName = emotionGvAdapter.getItem(position);
                int curPosition = et_write_status.getSelectionStart();
                StringBuilder stringBuilder = new StringBuilder(et_write_status.getText().toString());
                stringBuilder.insert(curPosition, emotionName);

//                et_write_status.setText(StringUtils.);
                // 特殊文字处理,将表情等转换一下
                et_write_status.setText(StringUtils.getWeiboContent(
                        this, et_write_status, stringBuilder.toString()));
                et_write_status.setSelection(curPosition + emotionName.length());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if(resultCode == RESULT_CANCELED){
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                } else {
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                }
                break;
            case ImageUtils.CROP_IMAGE:
                if(resultCode != RESULT_CANCELED){
                    imgUri.add(ImageUtils.cropImageUri);
                    updateImgs();
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if(resultCode != RESULT_CANCELED){
                    imgUri.add(data.getData());
                    updateImgs();
                }
                break;
            default:
                break;
        }
    }
}

package com.example.rogerzzzz.cityrecall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.rogerzzzz.cityrecall.adapter.EmotionGvAdapter;
import com.example.rogerzzzz.cityrecall.adapter.EmotionPagerAdapter;
import com.example.rogerzzzz.cityrecall.adapter.WriteStatusGridImgsAdapter;
import com.example.rogerzzzz.cityrecall.bean.MapItemBean;
import com.example.rogerzzzz.cityrecall.enity.Emotion;
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.utils.DialogUtils;
import com.example.rogerzzzz.cityrecall.utils.DisplayUtils;
import com.example.rogerzzzz.cityrecall.utils.ImageUtils;
import com.example.rogerzzzz.cityrecall.utils.L;
import com.example.rogerzzzz.cityrecall.utils.StringUtils;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.utils.VolleyErrorHelper;
import com.example.rogerzzzz.cityrecall.widget.UPlayer;
import com.example.rogerzzzz.cityrecall.widget.URecorder;
import com.example.rogerzzzz.cityrecall.widget.WrapHeightGridView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/3/17.
 */
public class ReleaseRecall extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LocationSource, AMapLocationListener {

    public double longtitude = 0;
    public double latitude   = 0;
    @Bind(R.id.toolbar)
    Toolbar            toolbar;
    @Bind(R.id.et_write_status)
    EditText           et_write_status;
    @Bind(R.id.gv_write_status)
    WrapHeightGridView gv_write_status;
    @Bind(R.id.iv_image)
    ImageView          iv_image;
    @Bind(R.id.iv_add)
    ImageView          iv_add;
    @Bind(R.id.iv_emoji)
    ImageView          iv_emoji;
    @Bind(R.id.ll_emotion_dashboard)
    LinearLayout       ll_emotion_dashboard;
    @Bind(R.id.vp_emotion_dashboard)
    ViewPager          vp_emotion_dashboard;
    @Bind(R.id.location_tv)
    TextView           address_tv;
    @Bind(R.id.map)
    MapView            mapView;
    @Bind(R.id.recordBtn)
    Button             recordBtn;
    @Bind(R.id.stopRecordingBtn)
    Button stopRecordingBtn;
    @Bind(R.id.playBtn)
    Button playBtn;
    @Bind(R.id.stopPlayingBtn)
    Button stopPlayingBtn;
    @Bind(R.id.rootLayout)
    LinearLayout rootLayout;
    private ProgressDialog             progressDialog;
    private WriteStatusGridImgsAdapter statusGridImgsAdapter;
    private EmotionPagerAdapter        emotionPagerAdapter;
    private AMap                       aMap;
    private OnLocationChangedListener  mListener;
    private AMapLocationClient         mLocationClient;
    private AMapLocationClientOption   mLocationOption;
    private ArrayList<Uri> imgUri = new ArrayList<Uri>();
    private String path = null;
    private URecorder recorder;
    private UPlayer   player;
    private boolean isRecorded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_recall);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        initRecordingModule();
        initView();
        initMap();
        initEmotion();
    }

    private void initRecordingModule(){
        String randomStr = UUID.randomUUID().toString();
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/ione.pcm";
        L.d(path);

        recorder = new URecorder(path);
        player = new UPlayer(path);

        recordBtn.setOnClickListener(this);
        stopRecordingBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        stopPlayingBtn.setOnClickListener(this);
    }

    private void initView() {
        toolbar.setTitle("评论列表");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_settings:
                        try {
                            sendStatus();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //进度条
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发送中..");
        //定位
        statusGridImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUri, gv_write_status, ReleaseRecall.this);
        gv_write_status.setAdapter(statusGridImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        iv_image.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
        }
    }

    //ToDo 发送逻辑
    private void sendStatus() throws FileNotFoundException, AVException {
        final ProgressDialog progressDialog = (ProgressDialog) DialogUtils.createLoadingDialog(ReleaseRecall.this);
        progressDialog.show();

        final String comment = et_write_status.getText().toString();
        final AVUser currentUser = AVUser.getCurrentUser();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showToast(this, "发送内容不能为空", Toast.LENGTH_SHORT);
            return;
        }

        if (address_tv.getText().toString().equals("正在定位")) {
            ToastUtils.showToast(this, "定位没有完成", Toast.LENGTH_SHORT);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                UserUtils.initCloudService(ReleaseRecall.this);
                String url = "http://yuntuapi.amap.com/datamanage/data/create";
                RequestQueue requestQueue = Volley.newRequestQueue(ReleaseRecall.this);
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String twitterID = JSON.parseObject(response).get("_id").toString();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    ArrayList<String> arr = null;
                                    arr = UserUtils.savePic(imgUri, ReleaseRecall.this);
                                    AVQuery<AVObject> query = new AVQuery<AVObject>("_File");
                                    query.whereContainedIn("name", arr);
                                    query.findInBackground(new FindCallback<AVObject>() {
                                        @Override
                                        public void done(List<AVObject> avObjects, AVException e) {
                                            if (e == null) {
                                                try {
                                                    String urlString;
                                                    if(avObjects.size() == 0){
                                                        urlString = "";
                                                    }else{
                                                        urlString = StringUtils.arrayListToString(avObjects);

                                                    }
                                                    final String finalUrlString = urlString;
                                                    if(isRecorded){
                                                        final AVFile file = AVFile.withAbsoluteLocalPath("record", path);
                                                        file.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                String recordUrl = file.getUrl();
                                                                AVObject recallItem = new AVObject("ReCall");
                                                                recallItem.put("username", currentUser.getUsername());
                                                                recallItem.put("content", comment);
                                                                recallItem.put("picString", finalUrlString);
                                                                recallItem.put("mapItemId", twitterID);
                                                                recallItem.put("radioPath", recordUrl);
                                                                recallItem.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(AVException e) {
                                                                        progressDialog.dismiss();
                                                                        ReleaseRecall.this.finish();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }else{
                                                        L.d("no record");
                                                        AVObject recallItem = new AVObject("ReCall");
                                                        recallItem.put("username", currentUser.getUsername());
                                                        recallItem.put("content", comment);
                                                        recallItem.put("picString", finalUrlString);
                                                        recallItem.put("mapItemId", twitterID);
                                                        recallItem.put("radioPath", "");
                                                        recallItem.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                progressDialog.dismiss();
                                                                ReleaseRecall.this.finish();
                                                            }
                                                        });
                                                    }
                                                } catch (FileNotFoundException e1) {
                                                    e1.printStackTrace();
                                                }
                                            } else if (e != null) {
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
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("bb", VolleyErrorHelper.getMessage(error, ReleaseRecall.this));
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        AVUser currentUser = AVUser.getCurrentUser();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("key", ServerParameter.CLOUDMAP_SERVICE_KEY);
                        map.put("tableid", ServerParameter.CLOUDMAP_DIY_TABLEID);
                        map.put("locType", "1");
                        MapItemBean mapItemBean = new MapItemBean();
                        mapItemBean.set_name(ServerParameter.CLOUDMAP_ITEM_NAME);
                        mapItemBean.set_address(address_tv.getText().toString());
                        mapItemBean.setContent(comment);
                        mapItemBean.set_location(longtitude + "," + latitude);
                        if(UserUtils.isUserLogin()){
                            mapItemBean.setUsername(currentUser.getUsername());
                        }else{
                            Log.d("Intent", "else");
                            Intent intent = new Intent(ReleaseRecall.this, WelcomPageActivity.class);
                            startActivity(intent);
                        }
                        String data = JSON.toJSONString(mapItemBean);
                        map.put("data", data);
                        return map;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }
                };
                requestQueue.add(request);
            }
        }.start();
    }

    //初始化表情面板内容
    private void initEmotion() {
        int gvWidth = DisplayUtils.getScreenWidthPixels(this);
        int spacing = DisplayUtils.dp2px(this, 8);
        //GridView 中表情的宽度
        int itemWidth = (gvWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        //遍历
        for (String emojiName : Emotion.emojiMap.keySet()) {
            emotionNames.add(emojiName);
            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);
                emotionNames = new ArrayList<String>();
            }
        }

        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        emotionPagerAdapter = new EmotionPagerAdapter(gvs);
        vp_emotion_dashboard.setAdapter(emotionPagerAdapter);
        //外面包裹一层layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);

    }

    private void updateImgs() {
        if (imgUri.size() > 0) {
            gv_write_status.setVisibility(View.VISIBLE);
            statusGridImgsAdapter.notifyDataSetChanged();
        } else {
            gv_write_status.setVisibility(View.GONE);
        }
    }

    //创建显示表情的GridView
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
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
        switch (view.getId()) {
            case R.id.iv_image:
                DialogUtils.showImagePickDialog(this);
                break;
            case R.id.iv_emoji:
                if (ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
                    iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
                    ll_emotion_dashboard.setVisibility(View.GONE);
                } else {
                    iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_add:
                break;
            case R.id.recordBtn:
                isRecorded = true;
                recorder.start();
                recordBtn.setVisibility(View.GONE);
                stopRecordingBtn.setVisibility(View.VISIBLE);
                Snackbar.make(rootLayout, "开始录音", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.stopRecordingBtn:
                recorder.stop();
                stopRecordingBtn.setVisibility(View.GONE);
                playBtn.setVisibility(View.VISIBLE);
                Snackbar.make(rootLayout, "录音结束", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.playBtn:
                player.start();
                playBtn.setVisibility(View.GONE);
                stopPlayingBtn.setVisibility(View.VISIBLE);
                Snackbar.make(rootLayout, "播放录音", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.stopPlayingBtn:
                player.stop();
                playBtn.setVisibility(View.VISIBLE);
                stopPlayingBtn.setVisibility(View.GONE);
                Snackbar.make(rootLayout, "停止播放", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object itemAdapter = adapterView.getAdapter();

        if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
            if (position == statusGridImgsAdapter.getCount() - 1) {
                DialogUtils.showImagePickDialog(this);
            }
        } else if (itemAdapter instanceof EmotionGvAdapter) {
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;
            if (position == emotionGvAdapter.getCount() - 1) {
                et_write_status.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                String emotionName = emotionGvAdapter.getItem(position);
                int curPosition = et_write_status.getSelectionStart();
                StringBuilder stringBuilder = new StringBuilder(et_write_status.getText().toString());
                stringBuilder.insert(curPosition, emotionName);

                //Todo 表情文字处理
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

        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                } else {
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                }
                break;
            case ImageUtils.CROP_IMAGE:
                if (resultCode != RESULT_CANCELED) {
                    imgUri.add(ImageUtils.cropImageUri);
                    updateImgs();
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if (resultCode != RESULT_CANCELED) {
                    imgUri.add(data.getData());
                    updateImgs();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                latitude = aMapLocation.getLatitude();
                longtitude = aMapLocation.getLongitude();
                address_tv.setText(aMapLocation.getAddress());
            } else {
                String errText = "定位失败，" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}

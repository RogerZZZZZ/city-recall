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
import com.example.rogerzzzz.cityrecall.utils.StringUtils;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.utils.VolleyErrorHelper;
import com.example.rogerzzzz.cityrecall.widget.WrapHeightGridView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rogerzzzz on 16/3/17.
 */
public class ReleaseRecall extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, LocationSource, AMapLocationListener {

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
    private TextView address_tv;

    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    public double longtitude = 0;
    public double latitude = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_recall);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        initView();
        initMap();
        initEmotion();
    }

    private void initView() {
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
        //定位
        address_tv = (TextView) findViewById(R.id.location_tv);

        statusGridImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUri, gv_write_status, ReleaseRecall.this);
        gv_write_status.setAdapter(statusGridImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        titlebar_tv_left.setOnClickListener(this);
        titlebar_tv_right.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
    }

    private void initMap(){
        if(aMap == null){
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

        if(address_tv.getText().toString().equals("正在定位")){
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
                        new Thread(){
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
                                            String urlString = "";
                                            if (e == null && avObjects.size() > 0) {
                                                urlString = StringUtils.arrayListToString(avObjects);
                                            } else if (e != null) {
                                                Log.d("失败", e.getMessage());
                                            }
                                            AVObject recallItem = new AVObject("ReCall");
                                            recallItem.put("username", currentUser.getUsername());
                                            recallItem.put("content", comment);
                                            recallItem.put("picString", urlString);
                                            recallItem.put("mapItemId", twitterID);
                                            recallItem.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    progressDialog.dismiss();
                                                    ReleaseRecall.this.finish();
                                                }
                                            });
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
                        mapItemBean.setUsername(currentUser.getUsername());
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
            case R.id.titlebar_tv_left:
                finish();
                break;
            case R.id.titlebar_tv_right:
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
        if(mListener != null && aMapLocation != null){
            if(aMapLocation != null && aMapLocation.getErrorCode() == 0){
                latitude = aMapLocation.getLatitude();
                longtitude = aMapLocation.getLongitude();
                address_tv.setText(aMapLocation.getAddress());
            }else{
                String errText = "定位失败，" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if(mLocationClient == null){
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
        if(mLocationClient != null){
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

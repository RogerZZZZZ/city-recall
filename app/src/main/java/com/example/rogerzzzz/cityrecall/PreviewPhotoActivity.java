package com.example.rogerzzzz.cityrecall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.rogerzzzz.cityrecall.utils.ImageCacheUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rogerzzzz on 16/3/31.
 */
public class PreviewPhotoActivity extends Activity implements View.OnClickListener{
//    private ArrayList<NetworkImageView> list = null;
    private ArrayList<ImageView> list = null;
    private MyPageAdapter adapter;
    private int pageIndex = 0;
    private int pageCount = 0;
    private ViewPager viewPager;
    private TextView mTitle_tv;
    private RequestQueue requestQueue;
    private Button backBtn;
    private List<String> picList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.preview_photo_activity);
        getIntentData();
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        mTitle_tv = (TextView) findViewById(R.id.title_des_text);
        mTitle_tv.getPaint().setFakeBoldText(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager_photo);
        viewPager.setOnPageChangeListener(pageChangeListener);
        backBtn = (Button) findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        pageCount = picList.size();
        for(int i = 0; i < pageCount; i++){
            initListView();
        }

        adapter = new MyPageAdapter(list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pageIndex);

        if(pageCount > 0){
            mTitle_tv.setText((pageIndex +  1) + "/" + pageCount);
        }

        if(pageIndex == 0){
            downloadImage(pageIndex);
        }
    }

    private void getIntentData(){
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        picList = Arrays.asList(intent.getStringExtra("picUrl").split(","));
        pageIndex = intent.getIntExtra("position", 0);
    }

    @SuppressLint({"NewApi", "InlinedApi"})
    private void initListView(){
        if(list == null){
            list = new ArrayList<ImageView>();
        }
        ImageView img = new ImageView(this);
        img.setBackgroundColor(0xff000000);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        list.add(img);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            downloadImage(position);
            pageIndex = position;
            mTitle_tv.setText((pageIndex + 1) + "/" + pageCount);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void downloadImage(int index){
        String imageUrl = picList.get(index);
        Picasso.with(PreviewPhotoActivity.this).load(imageUrl).into(list.get(index));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent intent = new Intent(PreviewPhotoActivity.this, StatusDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    private class MyPageAdapter extends PagerAdapter {

        private ArrayList<ImageView> listViews;// content

        private int size;// 页数

        public MyPageAdapter(ArrayList<ImageView> listViews) {// 构造函数
            // 初始化viewpager的时候给的一个页面
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {// 返回数量
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
            ((ViewPager) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {// 返回view对象
            try {
                ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
                Log.d("adapter","asd");

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}

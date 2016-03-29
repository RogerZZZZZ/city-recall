package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudImage;
import com.amap.api.services.cloud.CloudItem;
import com.android.volley.toolbox.ImageLoader;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rogerzzzz on 16/3/29.
 */
public class StatusDetailActivity extends Activity implements View.OnClickListener{
    private GridView mgridView;
    private TextView username_tv;
    private TextView content_tv;
    private TextView address_tv;
    private ImageView potrait_pic;
    private LinearLayout favour_layout;
    private LinearLayout comment_layout;
    private TextView time_tv;

    private CloudItem cloudItem;
    private List<CloudImage> cloudImages;
    private ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_detail);

        init();
    }

    private void init(){
        new TitleBuilder(this)
                .setTitleText("")
                .setLeftText("返回")
                .setLeftOnClickListener(this)
                .build();
        UserUtils.initCloudService(this);

        username_tv = (TextView) findViewById(R.id.username);
        content_tv = (TextView) findViewById(R.id.content);
        potrait_pic = (ImageView) findViewById(R.id.potrait_pic);
        favour_layout = (LinearLayout) findViewById(R.id.favour_layout);
        comment_layout = (LinearLayout) findViewById(R.id.comment_layout);
        time_tv = (TextView) findViewById(R.id.time);
        mgridView = (GridView) findViewById(R.id.grid);
        address_tv = (TextView) findViewById(R.id.location_tv);

        cloudItem = getIntent().getParcelableExtra("detailObject");
        if(cloudItem == null){
            this.finish();
            return;
        }

        String id = cloudItem.getID();
        String address = cloudItem.getSnippet();
        String content = "", username = "";
        String createTime = cloudItem.getCreatetime();
        Iterator iterator = cloudItem.getCustomfield().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            //Todo favour parameter
            if(key.equals("content")){
                content = val.toString();
            }else if(key.equals("username")){
                username = val.toString();
            }
        }
        username_tv.setText(username);
        content_tv.setText(content);
        address_tv.setText(address);
        time_tv.setText(createTime);

        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException e) {
                if(e == null){
                    AVFile avFile = avObjects.get(0).getAVFile("avatar");
                    String url = avFile.getUrl();
                    Log.d("url", url);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.titlebar_tv_left:
                finish();
                break;
            case R.id.comment_layout:
                break;
            case R.id.favour_layout:
                break;
        }

    }
}

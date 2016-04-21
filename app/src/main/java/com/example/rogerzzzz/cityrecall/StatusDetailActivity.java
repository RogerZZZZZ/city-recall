package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudItem;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.DetailPicGridviewAdapter;
import com.example.rogerzzzz.cityrecall.utils.BitmapHelper;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rogerzzzz on 16/3/29.
 */
public class StatusDetailActivity extends Activity implements View.OnClickListener {
    private GridView     mgridView;
    private TextView     username_tv;
    private TextView     content_tv;
    private TextView     address_tv;
    private ImageView    potrait_pic;
    private LinearLayout favour_layout;
    private LinearLayout not_favour_layout;
    private LinearLayout comment_layout;
    private TextView     time_tv;
    private CloudItem    cloudItem;
    private String       picUrl;
    private List<String> picList;
    private MyTask       task;
    private FavourTask   favourTask;
    private FavourTask   favourTaskNot;

    private String username;
    private String id;
    private AVObject favourObject = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_detail);
        init();
    }

    private void init() {
        new TitleBuilder(this)
                .setTitleText("详情")
                .setLeftText("返回")
                .setLeftOnClickListener(this)
                .build();
        UserUtils.initCloudService(this);

        username_tv = (TextView) findViewById(R.id.username);
        content_tv = (TextView) findViewById(R.id.content);
        potrait_pic = (ImageView) findViewById(R.id.potrait_pic);
        favour_layout = (LinearLayout) findViewById(R.id.favour_layout);
        not_favour_layout = (LinearLayout) findViewById(R.id.favour_layout_not);
        comment_layout = (LinearLayout) findViewById(R.id.comment_layout);
        time_tv = (TextView) findViewById(R.id.time);
        mgridView = (GridView) findViewById(R.id.grid);
        address_tv = (TextView) findViewById(R.id.location_tv);

        favour_layout.setOnClickListener(this);
        not_favour_layout.setOnClickListener(this);
        comment_layout.setOnClickListener(this);

        cloudItem = getIntent().getParcelableExtra("detailObject");
        if (cloudItem == null) {
            this.finish();
            return;
        }

        id = cloudItem.getID();
        String address = cloudItem.getSnippet();
        String content = "";
        String createTime = cloudItem.getCreatetime();
        Iterator iterator = cloudItem.getCustomfield().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (key.equals("content")) {
                content = val.toString();
            } else if (key.equals("username")) {
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
                if (e == null) {
                    AVFile avFile = avObjects.get(0).getAVFile("avatar");
                    final String url = avFile.getUrl();
                    task = new MyTask(url);
                    task.execute();
                } else {
                    e.printStackTrace();
                }
            }
        });

        //init favour module
        AVQuery<AVObject> statusFavours = new AVQuery<AVObject>("Favour");
        statusFavours.whereEqualTo("statusId", id);
        AVQuery<AVObject> individualFavous = new AVQuery<AVObject>("Favour");
        individualFavous.whereEqualTo("username", username);
        List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
        queries.add(statusFavours);
        queries.add(individualFavous);
        AVQuery<AVObject> mainQuery = AVQuery.and(queries);
        mainQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    if (avObjects.size() != 0) {
                        not_favour_layout.setVisibility(View.GONE);
                        favour_layout.setVisibility(View.VISIBLE);
                        favourObject = avObjects.get(0);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        //init picture wall module
        AVQuery<AVObject> query_pic = new AVQuery<AVObject>("ReCall");
        query_pic.whereEqualTo("mapItemId", id);
        query_pic.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null && avObjects.size() > 0) {
                    String picWallUrl = avObjects.get(0).get("picString").toString();
                    picUrl = picWallUrl;
                    List<String> list = Arrays.asList(picWallUrl.split(","));
                    picList = list;
                    setGridView(list);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(false);
        }
    }

    private void setGridView(List<String> list) {
        int size = list.size();
        int length = 60;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWith = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        mgridView.setLayoutParams(params);
        mgridView.setColumnWidth(itemWith);
        mgridView.setHorizontalSpacing(5);
        mgridView.setStretchMode(GridView.NO_STRETCH);
        mgridView.setNumColumns(size);

        DetailPicGridviewAdapter adapter = new DetailPicGridviewAdapter(getApplicationContext(), list);
        mgridView.setAdapter(adapter);
        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showImage(i);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_tv_left:
                finish();
                break;
            case R.id.comment_layout:
                Intent intent = new Intent(StatusDetailActivity.this, CommentActivity.class);
                intent.putExtra("statusId", id);
                startActivity(intent);
                break;
            case R.id.favour_layout_not:
                favourTask = new FavourTask(0);
                favourTask.execute();
                break;
            case R.id.favour_layout:
                if (favourObject != null) {
                    favourTaskNot = new FavourTask(1, favourObject);
                } else {
                    favourTaskNot = new FavourTask(1);
                }
                if (favourTaskNot != null && favourTaskNot.getStatus() != AsyncTask.Status.RUNNING) {
                    favourTaskNot.execute();
                }
                break;
            default:
                break;
        }
    }

    private void showImage(int position) {
        if (picList.size() != 0) {
            Intent intent = new Intent(StatusDetailActivity.this, PreviewPhotoActivity.class);
            intent.putExtra("picUrl", picUrl);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(false);
        }
        if (favourTask != null && favourTask.getStatus() != AsyncTask.Status.FINISHED) {
            favourTask.cancel(false);
        }
        if (favourTaskNot != null && favourTaskNot.getStatus() != AsyncTask.Status.FINISHED) {
            favourTaskNot.cancel(false);
        }
    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        private String url = "";
        private Bitmap bitmap;

        public MyTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... strings) {
            bitmap = BitmapHelper.getBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            potrait_pic.setImageBitmap(bitmap);
        }
    }

    private class FavourTask extends AsyncTask<String, Void, Void> {
        private int flag; //1:cancel 0: favour
        private AVObject temObject = null;
        private AVQuery<AVObject>       statusFavours;
        private AVQuery<AVObject>       individualFavous;
        private List<AVQuery<AVObject>> queries;
        private AVQuery<AVObject>       mainQuery;

        public FavourTask(int flag) {
            this.flag = flag;
        }

        public FavourTask(int flag, AVObject temObject) {
            this.temObject = temObject;
            this.flag = flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusFavours = new AVQuery<AVObject>("Favour");
            statusFavours.whereEqualTo("statusId", id);
            individualFavous = new AVQuery<AVObject>("Favour");
            individualFavous.whereEqualTo("username", username);
            queries = new ArrayList<AVQuery<AVObject>>();
            queries.add(statusFavours);
            queries.add(individualFavous);
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (flag == 1) {
                //cancel
                if (temObject != null) {
                    temObject.deleteInBackground();
                } else {
                    mainQuery = AVQuery.and(queries);
                    mainQuery.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> avObjects, AVException e) {
                            if (e == null && avObjects.size() != 0) {
                                avObjects.get(0).deleteInBackground();
                            }
                        }
                    });
                }
            } else {
                //favour
                AVObject post = new AVObject("Favour");
                post.put("username", username);
                post.put("statusId", id);
                post.saveInBackground();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (flag == 1) {
                //cancel
                favour_layout.setVisibility(View.GONE);
                not_favour_layout.setVisibility(View.VISIBLE);
            } else {
                //favour
                favour_layout.setVisibility(View.VISIBLE);
                not_favour_layout.setVisibility(View.GONE);
            }
        }
    }
}

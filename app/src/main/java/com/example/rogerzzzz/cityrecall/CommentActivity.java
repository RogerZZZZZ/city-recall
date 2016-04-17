package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.CommentAdapter;
import com.example.rogerzzzz.cityrecall.adapter.CommentItemAdapter;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;

import java.util.List;

/**
 * Created by rogerzzzz on 16/3/31.
 */
public class CommentActivity extends Activity implements View.OnClickListener {
    private String         statusId;
    private TextView       titlebar_left;
//    private ListView       listView;
    private CommentAdapter commentAdapter;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private CommentItemAdapter commentItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_comment);
        init();
    }

    private void init() {
        new TitleBuilder(this)
                .setTitleText("评论")
                .setLeftText("返回")
                .setLeftOnClickListener(this);

        titlebar_left = (TextView) findViewById(R.id.titlebar_tv_left);
//        listView = (ListView) findViewById(R.id.pull_to_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.pull_to_refresh_recycle);

        relativeLayout = (RelativeLayout) findViewById(R.id.comment_edit_layout);

        titlebar_left.setOnClickListener(this);

        statusId = getIntent().getStringExtra("statusId");
        //query comment result
        AVQuery<AVObject> query = new AVQuery<AVObject>("Comment");
        query.whereEqualTo("statusId", statusId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if(e == null){
                    AVUser currentUser = AVUser.getCurrentUser();
//                    commentAdapter = new CommentAdapter(CommentActivity.this, avObjects, currentUser.getUsername());
//                    listView.setAdapter(commentAdapter);
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            listView.smoothScrollToPositionFromTop(i, 0);
//                            relativeLayout.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    registerForContextMenu(listView);
                    commentItemAdapter = new CommentItemAdapter(CommentActivity.this, avObjects, currentUser.getUsername());
//                    recyclerView.setLayoutManager(new LinearLayoutManager());
                    recyclerView.setAdapter(commentItemAdapter);

                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_tv_left:
                finish();
                break;
        }
    }
}

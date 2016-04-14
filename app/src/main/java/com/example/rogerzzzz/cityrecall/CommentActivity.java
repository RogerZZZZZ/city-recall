package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.CommentAdapter;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.widget.PullToRefreshListView;

import java.util.List;

/**
 * Created by rogerzzzz on 16/3/31.
 */
public class CommentActivity extends Activity implements View.OnClickListener {
    private final int idEdit   = 1;
    private final int idDelete = 2;
    private String statusId;
    private TextView              titlebar_left;
    private PullToRefreshListView listView;
    private CommentAdapter        commentAdapter;

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
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh);

        titlebar_left.setOnClickListener(this);
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        onCreate(null);
                    }
                }, 2000);
            }
        });

        statusId = getIntent().getStringExtra("statusId");
        //query comment result
        AVQuery<AVObject> query = new AVQuery<AVObject>("Comment");
        query.whereEqualTo("statusId", statusId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if(e == null){
                    AVUser currentUser = AVUser.getCurrentUser();
                    commentAdapter = new CommentAdapter(CommentActivity.this, avObjects, currentUser.getUsername());
                    listView.setAdapter(commentAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int index = listView.getFirstVisiblePosition();
                            View v = listView.getChildAt(i);
                            int top = (v == null) ? 0 : v.getTop();
                            listView.setSelectionFromTop(index, top);
                        }
                    });
                    registerForContextMenu(listView);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case idEdit:
                ToastUtils.showToast(CommentActivity.this, commentAdapter.getItem(info.position - 1) + "", Toast.LENGTH_SHORT);
                return true;
            case idDelete:
                ToastUtils.showToast(CommentActivity.this, commentAdapter.getItem(info.position - 1) + "", Toast.LENGTH_SHORT);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, idEdit, Menu.NONE, "edit");
        menu.add(Menu.NONE, idDelete, Menu.NONE, "delete");
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

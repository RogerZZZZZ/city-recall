package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rogerzzzz.cityrecall.adapter.CommentAdapter;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.widget.PullToRefreshListView;

/**
 * Created by rogerzzzz on 16/3/31.
 */
public class CommentActivity extends Activity implements View.OnClickListener {
    private final int idEdit   = 1;
    private final int idDelete = 2;
    private TextView              titlebar_left;
    private PullToRefreshListView listView;
    private CommentAdapter        commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                commentAdapter.loadData();
                listView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                    }
                }, 2000);
            }
        });

        commentAdapter = new CommentAdapter(CommentActivity.this);
        commentAdapter.loadData();
        listView.setAdapter(commentAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CommentAdapter.ViewHolder viewHolder = (CommentAdapter.ViewHolder) adapterView.getTag();
                if (viewHolder.name != null) {
                    ToastUtils.showToast(CommentActivity.this, viewHolder.name.getText(), Toast.LENGTH_SHORT);
                }
            }
        });
        registerForContextMenu(listView);
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

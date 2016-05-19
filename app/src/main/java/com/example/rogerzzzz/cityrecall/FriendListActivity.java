package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.FriendListAdapter;
import com.example.rogerzzzz.cityrecall.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/5/18.
 */
public class FriendListActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.friendlist)
    RecyclerView friendList;

    private LinearLayoutManager manager;
    private FriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        ButterKnife.bind(this);
        toolbar.setTitle("关注人列表");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        initList();
    }

    private void initList(){
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followeeQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if(e == null && list.size() > 0){
                    L.d("follow"+list.get(0).getObjectId());
                    friendListAdapter = new FriendListAdapter(list, FriendListActivity.this);
                    friendListAdapter.setOnItemClickListener(new FriendListAdapter.onRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClickListener(int position) {

                        }
                    });
                    friendList.setLayoutManager(manager);
                    friendList.setAdapter(friendListAdapter);
                }
            }
        });
    }
}

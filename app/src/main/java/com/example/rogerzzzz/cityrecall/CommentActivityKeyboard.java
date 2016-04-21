package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.CommentItemAdapter;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.SimpleCommonUtils;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.data.ImMsgBean;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.widget.AutoHeightBehavior;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.widget.SimpleAppsGridView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sj.keyboard.XhsEmoticonsKeyBoard;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by rogerzzzz on 16/4/20.
 */
public class CommentActivityKeyboard extends AppCompatActivity implements FuncLayout.OnFuncKeyBoardListener, AutoHeightLayout.OnMaxParentHeightChangeListener{
    @Bind(R.id.toolbar)
    Toolbar              toolbar;
    @Bind(R.id.pull_to_refresh_recycle)
    RecyclerView recyclerView;
    @Bind(R.id.ek_bar)
    XhsEmoticonsKeyBoard ekBar;
    private CommentItemAdapter commentItemAdapter;
    private String statusId;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_keyboard);
        ButterKnife.bind(this);
        toolbar.setTitle("评论列表");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(CommentActivityKeyboard.this, "test", Toast.LENGTH_SHORT);
            }
        });
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        initView();
    }

    private void initView(){
        initEmotionsKeyboaradBar();
        initRecyclerview();
        initCoordiantorLayout();
    }

    private void initEmotionsKeyboaradBar(){
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, null));
        ekBar.addOnFuncKeyBoardListener(this);
        ekBar.addFuncView(new SimpleAppsGridView(this));
        ekBar.setOnMaxParentHeightChangeListener(this);

        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OnSendBtnClick(ekBar.getEtChat().getText().toString());
                //Todo clear editText
                ekBar.getEtChat().setText("");
            }
        });

        // TEST
        ekBar.getEmoticonsToolBarView().addToolItemView(com.keyboard.view.R.drawable.icon_face_nomal, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CommentActivityKeyboard.this, "TEST", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerview(){
        statusId = getIntent().getStringExtra("statusId");
        //query comment result
        AVQuery<AVObject> query = new AVQuery<AVObject>("Comment");
        query.whereEqualTo("statusId", statusId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if(e == null){
                    AVUser currentUser = AVUser.getCurrentUser();
                    commentItemAdapter = new CommentItemAdapter(CommentActivityKeyboard.this, avObjects, currentUser.getUsername());
                    commentItemAdapter.setOnItemClickListener(new CommentItemAdapter.onRecycleViewItemClickListener() {
                        @Override
                        public void onItemClickListener(int position) {
                            Log.d("--->", position + "");
                            recyclerView.scrollToPosition(position);
                        }
                    });
                    recyclerView.setAdapter(commentItemAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void initCoordiantorLayout(){
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ekBar.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior instanceof AutoHeightBehavior) {
            ((AutoHeightBehavior) behavior).setOnDependentViewChangedListener(new AutoHeightBehavior.OnDependentViewChangedListener() {
                @Override
                public void onDependentViewChangedListener(CoordinatorLayout parent, View child, View dependency) {
                    parent.post(new Runnable() {
                        @Override
                        public void run() {
//                            scrollToBottom();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void OnFuncPop(int i) {

    }

    @Override
    public void OnFuncClose() {

    }

    @Override
    public void onMaxParentHeightChange(int i) {

    }

    private void scrollToBottom() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    //Todo send comment func
    private void OnSendBtnClick(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ImMsgBean bean = new ImMsgBean();
            bean.setContent(msg);
//            chattingListAdapter.addData(bean, true, false);
//            scrollToBottom();
        }
    }
}

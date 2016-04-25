package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.rogerzzzz.cityrecall.adapter.CommentItemAdapter;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.Constants;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.QqEmoticonsKeyBoard;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.QqUtils;
import com.sj.emoji.EmojiBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by rogerzzzz on 16/3/31.
 */
public class CommentActivity extends Activity implements View.OnClickListener, FuncLayout.OnFuncKeyBoardListener{
    @Bind(R.id.pull_to_refresh_recycle)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ek_bar)
    QqEmoticonsKeyBoard ekBar;
    private String              statusId;
    private CommentItemAdapter  commentItemAdapter;
    private LinearLayoutManager recycleLinearLayoutManager;
    private String commentTmpContent;
    private String commentToUser;
    private String commentFromUser;
    private SendCommentTask msendCommentTask;
    private List<AVObject> adapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        toolbar.setTitle("评论列表");
        recycleLinearLayoutManager = new LinearLayoutManager(this);
        recycleLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        init();
    }

    private void init() {
        initEmoticonKeyboardBar();
        initListView();
    }

    private void initEmoticonKeyboardBar(){
        QqUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(QqUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);

        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo send
                OnSendBtnClick(ekBar.getEtChat().getText().toString());
                commentTmpContent = ekBar.getEtChat().getText().toString();
                ekBar.getEtChat().setText("");
            }
        });

        ekBar.getEmoticonsToolBarView().removeAllViews();
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                QqUtils.delClick(ekBar.getEtChat());
            } else {
                if(o == null){
                    return;
                }
                if(actionType == Constants.EMOTICON_CLICK_BIGIMAGE){
                    if(o instanceof EmoticonEntity){
                        OnSendImage(((EmoticonEntity)o).getIconUri());
                    }
                } else {
                    String content = null;
                    if(o instanceof EmojiBean){
                        content = ((EmojiBean)o).emoji;
                    } else if(o instanceof EmoticonEntity){
                        content = ((EmoticonEntity)o).getContent();
                    }

                    if(TextUtils.isEmpty(content)){
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };

    private void initListView(){
        statusId = getIntent().getStringExtra("statusId");
        //query comment result
        AVQuery<AVObject> query = new AVQuery<AVObject>("Comment");
        query.whereEqualTo("statusId", statusId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> avObjects, AVException e) {
                if(e == null){
                    adapterList = avObjects;
                    final AVUser currentUser = AVUser.getCurrentUser();
                    commentItemAdapter = new CommentItemAdapter(CommentActivity.this, adapterList, currentUser.getUsername());
                    commentItemAdapter.setOnItemClickListener(new CommentItemAdapter.onRecycleViewItemClickListener() {
                        @Override
                        public void onItemClickListener(int position) {
                            recyclerView.scrollToPosition(position);
                        }
                    });
                    commentItemAdapter.setSendCommentClickListener(new CommentItemAdapter.sendCommentClickListener() {
                        @Override
                        public void onButtonClickListener(int position) {
                            AVObject tmpObject = avObjects.get(position);
                            commentFromUser = currentUser.getUsername();
                            commentToUser = tmpObject.get("from").toString();
                            ekBar.getEtChat().setHint("回复" + commentToUser + ":");
                        }
                    });
                    recyclerView.setAdapter(commentItemAdapter);
                    recyclerView.setLayoutManager(recycleLinearLayoutManager);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void OnSendBtnClick(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            msendCommentTask = new SendCommentTask();
            msendCommentTask.execute();
        }
    }

    private void OnSendImage(String image) {
        if (!TextUtils.isEmpty(image)) {
            OnSendBtnClick("[img]" + image);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_tv_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnFuncPop(int height) {

    }

    @Override
    public void OnFuncClose() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        ekBar.reset();
    }

    private class SendCommentTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            final AVObject avObject = new AVObject("Comment");
            avObject.put("content", commentTmpContent);
            avObject.put("statusId", statusId);
            avObject.put("from", commentFromUser);
            avObject.put("to", commentToUser);
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    adapterList.add(avObject);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            commentItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(msendCommentTask != null && msendCommentTask.getStatus() != AsyncTask.Status.FINISHED){
            msendCommentTask.cancel(false);
        }
    }
}

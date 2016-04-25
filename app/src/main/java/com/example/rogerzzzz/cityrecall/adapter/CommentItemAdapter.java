package com.example.rogerzzzz.cityrecall.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.QqUtils;

import java.util.List;

/**
 * Created by rogerzzzz on 16/4/17.
 */
public class CommentItemAdapter extends RecyclerView.Adapter<CommentItemAdapter.MyViewHolder>{
    private Activity activity;
    private List<AVObject> commentList;
    private String currentUsername;
    private onRecycleViewItemClickListener mItemClickListener;
    private sendCommentClickListener mSendCommentClickListener;

    //define onItemClickListener interface
    public static interface onRecycleViewItemClickListener{
        void onItemClickListener(int position);
    }

    public static interface sendCommentClickListener{
        void onButtonClickListener(int position);
    }

    public CommentItemAdapter(Activity activity, List<AVObject> commentList, String currentUsername){
        this.activity = activity;
        this.commentList = commentList;
        this.currentUsername = currentUsername;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_list_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view) {
            @Override
            void deleteComment(int position) {
                AVObject commentItem = commentList.get(position);
                commentItem.deleteInBackground();
                commentList.remove(position);
                notifyItemRemoved(position);
            }

            @Override
            void sendComment(int position) {
                if(mSendCommentClickListener != null){
                    mSendCommentClickListener.onButtonClickListener(position);
                }
            }
        };
        return holder;
    }

    public void setOnItemClickListener(onRecycleViewItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setSendCommentClickListener(sendCommentClickListener listener){
        this.mSendCommentClickListener = listener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final AVObject commentItem = commentList.get(position);
        if(commentItem.get("content").toString().equals("") || commentItem.get("content") == null) return;
        setContent(holder.content, commentItem.get("content").toString());
        holder.username_tv.setText(commentItem.get("from").toString());
        holder.position = position;

        if(commentItem.get("to") != null){
            holder.replyLayout.setVisibility(View.VISIBLE);
            holder.replyUsername_tv.setText(commentItem.get("to").toString());
        }
        if(commentItem.get("from").toString().equals(currentUsername)){
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setContent(TextView tv_content, String content){
        QqUtils.spannableEmoticonFilter(tv_content, content);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    abstract class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public String id;
        public TextView username_tv;
        public TextView replyUsername_tv;
        public TextView content;
        public LinearLayout replyLayout;
        public ImageView commentBtn;
        public ImageView deleteBtn;
        public int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
            username_tv = (TextView) itemView.findViewById(R.id.username);
            replyUsername_tv = (TextView) itemView.findViewById(R.id.reply_username);
            replyLayout = (LinearLayout) itemView.findViewById(R.id.reply_layout);
            commentBtn = (ImageView) itemView.findViewById(R.id.commentBtn);
            deleteBtn = (ImageView) itemView.findViewById(R.id.deleteBtn);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClickListener(position);
                    }
                }
            });
            commentBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.commentBtn:
                    sendComment(getAdapterPosition());
                    break;
                case R.id.deleteBtn:
                    deleteComment(getAdapterPosition());
                    break;
            }
        }

        abstract void deleteComment(int position);

        abstract void sendComment(int position);
    }
}

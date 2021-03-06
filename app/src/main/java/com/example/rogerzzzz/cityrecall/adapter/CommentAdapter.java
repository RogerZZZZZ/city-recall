package com.example.rogerzzzz.cityrecall.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.example.rogerzzzz.cityrecall.R;

import java.util.List;

/**
 * Created by rogerzzzz on 16/4/5.
 */
public class CommentAdapter extends BaseAdapter implements View.OnClickListener{
    private Activity          activity;
    private List<AVObject> commentList;
    private String currentUsername;

    public CommentAdapter(Activity activity, List<AVObject> commentList, String currentUsername) {
        this.activity = activity;
        this.commentList = commentList;
        this.currentUsername = currentUsername;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public AVObject getItem(int i) {
        return commentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View rowView = convertView;
        final AVObject commentItem = getItem(position);
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.comment_list_item, null);
            viewHolder.content = (TextView) rowView.findViewById(R.id.content);
            viewHolder.username_tv = (TextView) rowView.findViewById(R.id.username);
            viewHolder.replyUsername_tv = (TextView) rowView.findViewById(R.id.reply_username);
            viewHolder.replyLayout = (LinearLayout) rowView.findViewById(R.id.reply_layout);
            viewHolder.commentBtn = (ImageView) rowView.findViewById(R.id.commentBtn);
            viewHolder.deleteBtn = (ImageView) rowView.findViewById(R.id.deleteBtn);

            viewHolder.commentBtn.setOnClickListener(this);
            viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commentItem.deleteInBackground();
                    commentList.remove(position);
                    notifyDataSetChanged();
                }
            });

            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.content.setText(commentItem.get("content").toString());
        holder.username_tv.setText(commentItem.get("from").toString());
        if(commentItem.get("to") != null){
            holder.replyLayout.setVisibility(View.VISIBLE);
            holder.replyUsername_tv.setText(commentItem.get("to").toString());
        }
        if(commentItem.get("from").toString().equals(currentUsername)){
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commentBtn:
                Log.d("commentBtn", "click");
                break;
        }
    }

    public class ViewHolder {
        public String       id;
        public TextView     username_tv;
        public TextView     replyUsername_tv;
        public TextView     content;
        public LinearLayout replyLayout;
        public ImageView    commentBtn;
        public ImageView    deleteBtn;
    }
}

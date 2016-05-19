package com.example.rogerzzzz.cityrecall.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.utils.BitmapHelper;
import com.example.rogerzzzz.cityrecall.utils.L;

import java.util.List;

/**
 * Created by rogerzzzz on 16/5/18.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> {
    private List<AVUser> list;
    private Activity activity;
    private MyTask       task;
    private onRecyclerViewItemClickListener mItemClickListener;

    public FriendListAdapter(List<AVUser> list, Activity activity){
        L.d(list.get(0).getObjectId());
        this.list = list;
        this.activity = activity;
    }

    public static interface onRecyclerViewItemClickListener{
        void onItemClickListener(int position);
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.friendlist_adapter, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.position = position;
        AVQuery<AVUser> query = new AVQuery<>("_User");
        query.whereEqualTo("objectId", list.get(position).getObjectId());
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException e) {
                if (e == null) {
                    String username = avObjects.get(0).get("username").toString();
                    holder.username_tv.setText(username);
                    AVFile avFile = avObjects.get(0).getAVFile("avatar");
                    if(avFile != null){
                        final String url = avFile.getUrl();
                        task = new MyTask(url, holder.potrait_iv);
                        task.execute();
                    }else{
                        holder.potrait_iv.setImageResource(R.drawable.icon_default);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView potrait_iv;
        private TextView username_tv;
        public int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            potrait_iv = (ImageView) itemView.findViewById(R.id.potrait);
            username_tv = (TextView) itemView.findViewById(R.id.username);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClickListener(position);
                    }
                }
            });
        }
    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        private String url = "";
        private Bitmap bitmap;
        private ImageView iv;

        public MyTask(String url, ImageView iv) {
            this.url = url;
            this.iv = iv;
        }

        @Override
        protected Void doInBackground(String... strings) {
            bitmap = BitmapHelper.getBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iv.setImageBitmap(bitmap);
        }
    }
}

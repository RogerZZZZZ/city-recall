package com.example.rogerzzzz.cityrecall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rogerzzzz.cityrecall.R;

/**
 * Created by rogerzzzz on 16/3/19.
 */
public class LeftMenuAdapter extends BaseAdapter {
    private Context context;
    private int     resourceId;
    private int[]    settingIcons = new int[]{R.drawable.iconfont_detail, R.drawable.iconfont_detailcollected, R.drawable.iconfont_setting, R.drawable.iconfont_write, R.drawable.iconfont_logout};
    private String[] settingNames = new String[]{"个人信息", "个人收藏", "设置", "发送新动态", "登出"};

    public LeftMenuAdapter(Context context, int viewResourseId) {
        this.context = context;
        this.resourceId = viewResourseId;
    }

    @Override
    public int getCount() {
        return settingIcons.length;
    }

    @Override
    public Object getItem(int i) {
        return settingNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.left_menu_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.menu_item_img);
            viewHolder.imageView.setImageResource(settingIcons[position]);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.menu_item_name);
            viewHolder.textView.setText(settingNames[position]);
            convertView.setTag(viewHolder);
        }

        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView  textView;
    }
}

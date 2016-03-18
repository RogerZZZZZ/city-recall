package com.example.rogerzzzz.cityrecall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AbsListView.LayoutParams;

import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.enity.Emotion;

import java.util.List;

/**
 * Created by rogerzzzz on 16/3/18.
 */
public class EmotionGvAdapter extends BaseAdapter{
    private Context context;
    private List<String> emotionNames;
    private int itemWidth;

    public EmotionGvAdapter(Context context, List<String> emotionNames, int itemWidth) {
        this.context = context;
        this.emotionNames = emotionNames;
        this.itemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return emotionNames.size() + 1;
    }

    @Override
    public String getItem(int position) {
        return emotionNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv = new ImageView(context);
        LayoutParams params = new LayoutParams(itemWidth, itemWidth);
        iv.setPadding(itemWidth/8, itemWidth/8, itemWidth/8, itemWidth/8);
        iv.setLayoutParams(params);

        if(position == getCount() - 1) {
            iv.setImageResource(R.drawable.emotion_delete_icon);
        } else {
            String emotionName = emotionNames.get(position);
            iv.setImageResource(Emotion.getImgByName(emotionName));
        }
        return iv;
    }
}

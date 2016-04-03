package com.example.rogerzzzz.cityrecall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.rogerzzzz.cityrecall.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rogerzzzz on 16/3/30.
 */
public class DetailPicGridviewAdapter extends BaseAdapter{
    private Context context;
    private List<String> imageItems;

    public DetailPicGridviewAdapter(Context context, List<String> imageItems){
        this.context = context;
        this.imageItems = imageItems;
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public Object getItem(int i) {
        return imageItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.photo_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.ItemImage);
        Picasso.with(context).load(imageItems.get(position)).centerCrop().resize(50, 50).into(imageView);
        return view;
    }
}

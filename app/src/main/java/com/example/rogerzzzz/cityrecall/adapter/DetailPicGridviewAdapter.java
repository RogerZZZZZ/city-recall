package com.example.rogerzzzz.cityrecall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.utils.ImageCacheUtils;

import java.util.List;

/**
 * Created by rogerzzzz on 16/3/30.
 */
public class DetailPicGridviewAdapter extends BaseAdapter{
    private Context context;
    private ImageLoader imageLoader;
    private List<String> imageItems;

    public DetailPicGridviewAdapter(Context context, List<String> imageItems){
        this.context = context;
        this.imageItems = imageItems;
        imageLoader = new ImageLoader(Volley.newRequestQueue(context), new ImageCacheUtils());
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
        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.ItemImage);
        imageView.setDefaultImageResId(R.drawable.ic_launcher);
        imageView.setImageUrl(imageItems.get(position), imageLoader);
        return view;
    }
}

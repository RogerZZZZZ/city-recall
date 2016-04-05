package com.example.rogerzzzz.cityrecall.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rogerzzzz.cityrecall.R;

import java.util.ArrayList;

/**
 * Created by rogerzzzz on 16/4/5.
 */
public class CommentAdapter extends BaseAdapter {
    private ArrayList<String> items;
    private Activity          activity;

    public CommentAdapter(Activity activity) {
        this.activity = activity;
    }


    public void loadData() {
        items = new ArrayList<String>();

        items.add("Ajax Amsterdam");
        items.add("Barcelona");
        items.add("Manchester United");
        items.add("Chelsea");
        items.add("Real Madrid");
        items.add("Bayern Munchen");
        items.add("Internazionale");
        items.add("Valencia");
        items.add("Arsenal");
        items.add("AS Roma");
        items.add("Tottenham Hotspur");
        items.add("PSV");
        items.add("Olympique Lyon");
        items.add("AC Milan");
        items.add("Dortmund");
        items.add("Schalke 04");
        items.add("Twente");
        items.add("Porto");
        items.add("Juventus");

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        String record = (String) getItem(position);
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.comment_list_item, null);
            viewHolder.name = (TextView) rowView.findViewById(R.id.textView1);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.name.setText(record);
        return rowView;
    }

    public class ViewHolder {
        public String   id;
        public TextView name;
    }
}

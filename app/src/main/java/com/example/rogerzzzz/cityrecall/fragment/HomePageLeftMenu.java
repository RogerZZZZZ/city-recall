package com.example.rogerzzzz.cityrecall.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.adapter.LeftMenuAdapter;

/**
 * Created by rogerzzzz on 16/3/19.
 */
public class HomePageLeftMenu extends Fragment{
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.homepage_left_menu, container, false);
        }

        LeftMenuAdapter leftMenuAdapter = new LeftMenuAdapter(getActivity(), R.layout.left_menu_item);
        ListView listView = (ListView) view.findViewById(R.id.menu_item_list);
        listView.setAdapter(leftMenuAdapter);

        return view;
    }

}

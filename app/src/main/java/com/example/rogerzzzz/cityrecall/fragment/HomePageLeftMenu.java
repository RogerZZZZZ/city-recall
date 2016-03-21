package com.example.rogerzzzz.cityrecall.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.rogerzzzz.cityrecall.LoginActivity;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.ReleaseRecall;
import com.example.rogerzzzz.cityrecall.adapter.LeftMenuAdapter;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;

/**
 * Created by rogerzzzz on 16/3/19.
 */
public class HomePageLeftMenu extends Fragment implements AdapterView.OnItemClickListener{
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.homepage_left_menu, container, false);
        }

        LeftMenuAdapter leftMenuAdapter = new LeftMenuAdapter(getActivity(), R.layout.left_menu_item);
        ListView listView = (ListView) view.findViewById(R.id.menu_item_list);
        listView.setAdapter(leftMenuAdapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object itemAdapter = adapterView.getAdapter();
        if(itemAdapter instanceof LeftMenuAdapter){
            switch(position){
                case 0:
                    ToastUtils.showToast(getActivity(), "personal information", Toast.LENGTH_SHORT);
                    AVUser.logOut();
                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent2);
                    break;
                case 1:
                    ToastUtils.showToast(getActivity(), "personal collection", Toast.LENGTH_SHORT);
                    break;
                case 2:
                    ToastUtils.showToast(getActivity(), "Setting", Toast.LENGTH_SHORT);
                    break;
                case 3:
                    Intent intent = new Intent(getActivity(), ReleaseRecall.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
}

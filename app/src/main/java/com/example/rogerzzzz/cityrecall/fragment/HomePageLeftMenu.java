package com.example.rogerzzzz.cityrecall.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVUser;
import com.example.rogerzzzz.cityrecall.LoginActivity;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.ReleaseRecall;
import com.example.rogerzzzz.cityrecall.adapter.LeftMenuAdapter;
import com.example.rogerzzzz.cityrecall.bean.MapItemBean;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.utils.VolleyErrorHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rogerzzzz on 16/3/19.
 */
public class HomePageLeftMenu extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private View view;
    private TextView textView;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.homepage_left_menu, container, false);
        }

        LeftMenuAdapter leftMenuAdapter = new LeftMenuAdapter(getActivity(), R.layout.left_menu_item);
        ListView listView = (ListView) view.findViewById(R.id.menu_item_list);
        textView = (TextView) view.findViewById(R.id.user_username);
        imageView = (ImageView) view.findViewById(R.id.potrait_icon);
        listView.setAdapter(leftMenuAdapter);
        listView.setOnItemClickListener(this);

        imageView.setOnClickListener(this);

        AVUser currentUser = AVUser.getCurrentUser();
        if(UserUtils.isUserLogin()){
            textView.setText(currentUser.getUsername());
        }else{
            textView.setText("登陆/注册");
        }

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
                    volleyPost();
                    break;
                case 3:
                    Intent intent = new Intent(getActivity(), ReleaseRecall.class);
                    startActivity(intent);
                    break;
                case 4:
                    UserUtils.userLogout();
                    Intent intent_logout = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent_logout);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.potrait_icon:
                //Todo 个人信息页面
                break;
        }
    }

    private void volleyPost(){
        String url = "http://yuntuapi.amap.com/datamanage/data/create";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("aa", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("bb", VolleyErrorHelper.getMessage(error, getActivity()));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                AVUser currentUser = AVUser.getCurrentUser();
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", "5b53128b4f4b0f122d198544defe6c59");
                map.put("tableid", "56e908e4305a2a32886fcb10");
                map.put("locType", "1");

                MapItemBean mapItemBean = new MapItemBean();
                mapItemBean.set_name("myCity");
                mapItemBean.set_address("myCity");
                mapItemBean.setContent("asd");
                mapItemBean.set_location("104.394729,31.125698");
                mapItemBean.setUsername(currentUser.getUsername());
                String data = JSON.toJSONString(mapItemBean);
                Log.i("data", data);
                map.put("data", data);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
//        request.setTag("mapRequest");
        requestQueue.add(request);
    }
}

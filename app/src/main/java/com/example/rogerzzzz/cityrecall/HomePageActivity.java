package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.rogerzzzz.cityrecall.fragment.HomePageLeftMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * Created by rogerzzzz on 16/3/18.
 */
public class HomePageActivity extends SlidingFragmentActivity {
    private MapView mapView;
    private AMap aMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homepage);
        initLeftMenu();
        initMap(savedInstanceState);
    }

    private void initLeftMenu(){
        Fragment leftMenu = new HomePageLeftMenu();
        setBehindContentView(R.layout.left_menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.id_left_menu_frame, leftMenu).commit();
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
    }

    private void initMap(Bundle savedInstanceState){
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if(aMap == null){
            aMap = mapView.getMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void showLeftMenu(View view){
        getSlidingMenu().showMenu();
    }

}

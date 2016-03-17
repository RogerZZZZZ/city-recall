package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;


public class MainActivity extends ActionBarActivity {

    private MapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        initMap();
        initCloudService();

    }

    private void initMap(){
        if(aMap == null){
            aMap = mapView.getMap();
        }
    }

    private void initCloudService(){
        AVOSCloud.initialize(this, "vbq8HEM7nninw4GP4K3IkNiW-gzGzoHsz", "WruY52lpBmOthvNASpSPJrM3");
        AVObject testOj = new AVObject("TestOj");
        testOj.put("foo", "bar");
        testOj.saveInBackground();
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
}

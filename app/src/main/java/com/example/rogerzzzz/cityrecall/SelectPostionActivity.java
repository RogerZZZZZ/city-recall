package com.example.rogerzzzz.cityrecall;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/5/19.
 */
public class SelectPostionActivity extends AppCompatActivity implements AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener{

    @Bind(R.id.map)
    MapView mapView;

    @Bind(R.id.poi_detail)
    RelativeLayout detailLayout;

    @Bind(R.id.poi_distance)
    TextView positionName_tv;

    @Bind(R.id.rootLayout)
    LinearLayout rootLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private AMap aMap;
    private GeocodeSearch geocodeSearch;
    private LatLng resultLatlng;
    private int flag; //0:list 1:map 2:send new status


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position);
        ButterKnife.bind(this);
        toolbar.setTitle("选择位置");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mapView.onCreate(savedInstanceState);
        initMap();
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        aMap.clear();
        detailLayout.setVisibility(View.VISIBLE);
        aMap.addMarker(new MarkerOptions()
            .anchor(0.5f, 0.5f)
            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding)))
            .position(latLng));
        resultLatlng = latLng;
        getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
    }

    private void getAddress(LatLonPoint latLonPoint){
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    private void initMap(){
        if(aMap == null){
            aMap = mapView.getMap();
            aMap.setOnMapClickListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                positionName_tv.setText(result.getRegeocodeAddress().getFormatAddress());
            } else {
                Snackbar.make(rootLayout, "无结果", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(rootLayout, "网络出错", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}

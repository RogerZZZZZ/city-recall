package com.example.rogerzzzz.cityrecall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.fragment.HomePageLeftMenu;
import com.example.rogerzzzz.cityrecall.utils.AMapUtil;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.CloudOverlay;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rogerzzzz on 16/3/18.
 */
public class HomePageActivity extends SlidingFragmentActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener,
        AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, CloudSearch.OnCloudSearchListener, View.OnClickListener{
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLonPoint lp;
    private CloudSearch cloudSearch;
    private CloudSearch.Query query;
    private CloudOverlay cloudOverlay;
    private List<CloudItem> mCloudItems;
    private ProgressDialog progressDialog;
    private Marker mCloudIDMarker;
    private String TAG = "CityRecall";
    private ArrayList<CloudItem> items = new ArrayList<CloudItem>();
    private String tableId = ServerParameter.CLOUDMAP_DIY_TABLEID;
    private Marker detailMarker;
    private Marker mlastMarker;
    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress, mPoiFavour;
    private LinearLayout jumpLayout;
    private String keyWord = ServerParameter.CLOUD_SERVICE_KEYWORD;
    private boolean isInitNearbySearch = false;

    public double longtitude;
    public double latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homepage);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initLeftMenu();
        initMap();
        UserUtils.initCloudService(HomePageActivity.this);
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

    private void initMap(){
        if(aMap == null){
            aMap = mapView.getMap();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
            aMap.setOnMapClickListener(this);
            aMap.setOnMarkerClickListener(this);
            aMap.setInfoWindowAdapter(this);
            aMap.setOnInfoWindowClickListener(this);
            mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
            mPoiName = (TextView) findViewById(R.id.poi_name);
            mPoiAddress = (TextView) findViewById(R.id.poi_address);
            mPoiFavour = (TextView) findViewById(R.id.poi_favour);
            jumpLayout = (LinearLayout) findViewById(R.id.jump_layout);
            jumpLayout.setOnClickListener(this);

            cloudSearch = new CloudSearch(this);
            cloudSearch.setOnCloudSearchListener(this);
        }
    }

    private void showProgressDialog(){
        if(progressDialog == null) progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("\n正在搜索   ");
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public void searchByBound() {
        showProgressDialog();
        items.clear();
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(
                lp.getLatitude(), lp.getLongitude()), 4000);
        try {
            query = new CloudSearch.Query(tableId, keyWord, bound);
            query.setPageSize(10);
            CloudSearch.Sortingrules sorting = new CloudSearch.Sortingrules(
                    "_id", false);
            query.setSortingrules(sorting);
            cloudSearch.searchCloudAsyn(query);// 异步搜索
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
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
        whetherToShowDetailInfo(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void showLeftMenu(View view){
        getSlidingMenu().showMenu();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        aMap.clear();
        mListener = onLocationChangedListener;
        if(mLocationClient == null){
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if(mLocationClient != null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(mListener != null && aMapLocation != null){
            if(aMapLocation != null && aMapLocation.getErrorCode() == 0){
                mListener.onLocationChanged(aMapLocation);
                longtitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();
                if(!isInitNearbySearch){
                    lp = new LatLonPoint(latitude, longtitude);
                    searchByBound();
                    isInitNearbySearch = true;
                }
            }else{
                String errText = "定位失败，" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
        if(mlastMarker != null){
            resetLastMarker();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getObject() != null){
            whetherToShowDetailInfo(true);
            try{
                CloudItem mCurrentPoi = (CloudItem) marker.getObject();
                if(mlastMarker == null){
                    mlastMarker = marker;
                }else{
                    resetLastMarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));
                setPoiItemDisplayContent(mCurrentPoi);
            }catch (Exception e){
                //Todo handle exception
                e.printStackTrace();
            }
        }else{
            whetherToShowDetailInfo(false);
            resetLastMarker();
        }
        return true;
    }

    private void setPoiItemDisplayContent(final CloudItem mCurrentPoi) {
        Iterator iterator =  mCurrentPoi.getCustomfield().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            //Todo favour parameter
            if(key.equals("content")){
                mPoiAddress.setText(val.toString());
            }else if(key.equals("username")){
                mPoiName.setText(val.toString());
            }
        }
    }

    private void whetherToShowDetailInfo(boolean isToShow){
        if(isToShow){
            mPoiDetail.setVisibility(View.VISIBLE);
        }else{
            mPoiDetail.setVisibility(View.GONE);
        }
    }

    private void resetLastMarker(){
        mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding)));
        mlastMarker = null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String tile = marker.getTitle();
        for (CloudItem item : items) {
            if (tile.equals(item.getTitle())) {
                Intent intent = new Intent(HomePageActivity.this,
                        HomePageActivity.class);
                intent.putExtra("clouditem", item);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onCloudSearched(CloudResult result, int code) {
        dismissProgressDialog();
        if(code == 1000){
            if(result != null && result.getQuery() != null){
                if(result.getQuery().equals(query)){
                    mCloudItems = result.getClouds();
                    if(mCloudItems != null && mCloudItems.size() > 0){
                        cloudOverlay = new CloudOverlay(aMap, mCloudItems, getResources());
                        cloudOverlay.removeFromMap();
                        cloudOverlay.addToMap();
                        for(CloudItem item : mCloudItems){
                            items.add(item);
                        }
                        if(query.getBound().getShape().equals(CloudSearch.SearchBound.BOUND_SHAPE)){
                            aMap.addCircle(new CircleOptions().center(new LatLng(lp.getLatitude(), lp.getLongitude()))
                                    .radius(5000).strokeColor(Color.BLACK)
                                    .strokeWidth(3));
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lp.getLatitude(),
                                            lp.getLongitude()), 12));
                        }
                    }else{
                        ToastUtils.showToast(this, "无结果", Toast.LENGTH_SHORT);
                    }
                }
            }else{
                ToastUtils.showToast(this, "无结果", Toast.LENGTH_SHORT);
            }
        }else{
            ToastUtils.showToast(this, "无结果", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int code) {
        dismissProgressDialog();
        if(code == 1000 && item != null){
            if(mCloudIDMarker != null){
                mCloudIDMarker.destroy();
            }
            LatLng position = AMapUtil.convertToLatLng(item.getLatLonPoint());
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(position, 18, 0, 30)));
            mCloudIDMarker = aMap.addMarker(new MarkerOptions().position(position)
                        .title(item.getTitle())
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding))));
            items.add(item);
        }else{
            ToastUtils.showToast(HomePageActivity.this, code + "", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.jump_layout:
                Intent intent = new Intent(HomePageActivity.this, StatusDetailActivity.class);
                startActivity(intent);
                break;
        }
    }
}

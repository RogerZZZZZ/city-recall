package com.example.rogerzzzz.cityrecall;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.rogerzzzz.cityrecall.fragment.HomePageLeftMenu;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rogerzzzz on 16/3/18.
 */
public class HomePageActivity extends SlidingFragmentActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener,
        AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, PoiSearch.OnPoiSearchListener{
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private PoiResult poiResult;
    private int currentPage = 0;
    private PoiSearch.Query query;
    private LatLonPoint lp;
    private myPoiOverlay  poiOverlay;
    private Marker locationMarker;
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private List<PoiItem> poiItem;
    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;
    private String keyWord = "myCity";

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
        doSearchQuery();
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
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
            aMap.setOnMapClickListener(this);
            aMap.setOnMarkerClickListener(this);
            aMap.setInfoWindowAdapter(this);
            mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
            mPoiName = (TextView) findViewById(R.id.poi_name);
            mPoiAddress = (TextView) findViewById(R.id.poi_address);
        }
    }

    protected void doSearchQuery(){
        query = new PoiSearch.Query(keyWord, "", "");
        query.setPageSize(20);
        query.setPageNum(currentPage);
        if(lp != null){
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
            poiSearch.searchPOIAsyn();
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
                lp = new LatLonPoint(longtitude, latitude);
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
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
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
            }
        }else{
            whetherToShowDetailInfo(false);
            resetLastMarker();
        }
        return true;
    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {
        Log.d("code--:", code + "");
        if(code == 1000){
            if(result != null && result.getQuery() != null){
                if(result.getQuery().equals(query)){
                    poiResult = result;
                    poiItem = poiResult.getPois();
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();
                    if(poiItem != null && poiItem.size() > 0){
                        whetherToShowDetailInfo(false);
                        if(mlastMarker != null){
                            resetLastMarker();
                        }
                        if(poiOverlay != null){
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new myPoiOverlay(aMap, poiItem);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if(suggestionCities != null && suggestionCities.size() > 0){
                        showSuggestCity(suggestionCities);
                    }else{
                        ToastUtils.showToast(HomePageActivity.this, "无结果", Toast.LENGTH_SHORT);
                    }
                }
            }else{
                ToastUtils.showToast(HomePageActivity.this, "无结果", Toast.LENGTH_SHORT);
            }
        }
    }

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
    }

    private void showSuggestCity(List<SuggestionCity> cities){
        String information = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            information += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtils.showToast(HomePageActivity.this, information, Toast.LENGTH_SHORT);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

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

    private class myPoiOverlay{
        private AMap maMap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarkers = new ArrayList<Marker>();

        public myPoiOverlay(AMap amap, List<PoiItem> pois){
            this.maMap = amap;
            this.mPois = pois;
        }

        /*
        * 添加Marker到地图上
         */
        public void addToMap(){
            for(int i = 0; i < mPois.size(); i++){
                Marker marker = maMap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarkers.add(marker);
            }
        }

        /*
        * remove all markers
         */
        public void removeFromMap(){
            for(Marker marker : mPoiMarkers){
                marker.remove();
            }
        }

        /*
        * 移动镜头到当前的视角
         */
        public void zoomToSpan(){
            if(mPois != null && mPois.size() > 0){
                if(maMap == null) return;
                LatLngBounds bounds = getLatLngBounds();
                maMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds(){
            LatLngBounds.Builder b = LatLngBounds.builder();
            for(int i = 0; i < mPois.size(); i++){
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(), mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index){
            return new MarkerOptions()
                    .position(new LatLng(mPois.get(index).getLatLonPoint().getLatitude(), mPois.get(index).getLatLonPoint().getLongitude()))
                    .title(getTitle(index))
                    .snippet(getSnippet(index))
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding)));
        }

        protected String getTitle(int index){
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index){
            return mPois.get(index).getSnippet();
        }

        public int getPoiIndex(Marker marker){
            for(int i = 0; i < mPoiMarkers.size(); i++){
                if(mPoiMarkers.get(i).equals(marker)){
                    return i;
                }
            }
            return -1;
        }

        public PoiItem getPoiItem(int index){
            if(index < 0 || index >= mPois.size()){
                return null;
            }
            return mPois.get(index);
        }


    }

}

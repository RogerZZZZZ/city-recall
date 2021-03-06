package com.example.rogerzzzz.cityrecall.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.rogerzzzz.cityrecall.HomeActivity;
import com.example.rogerzzzz.cityrecall.HotStatusActivity;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.ReleaseRecall;
import com.example.rogerzzzz.cityrecall.SelectPostionActivity;
import com.example.rogerzzzz.cityrecall.StatusDetailActivity;
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.utils.AMapUtil;
import com.example.rogerzzzz.cityrecall.utils.L;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.CloudOverlay;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rogerzzzz on 16/5/6.
 */
public class HomePageMapFragment extends Fragment implements LocationSource, AMapLocationListener, AMap.OnMapClickListener,
        AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, CloudSearch.OnCloudSearchListener, View.OnClickListener{

    public  double                    longtitude;
    public  double                    latitude;
    private MapView                   mapView;
    private AMap                      aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient        mLocationClient;
    private AMapLocationClientOption  mLocationOption;
    private LatLonPoint               lp;
    private CloudSearch               cloudSearch;
    private CloudSearch.Query         query;
    private CloudOverlay              cloudOverlay;
    private List<CloudItem>           mCloudItems;
    private ProgressDialog            progressDialog;
    private Marker                    mCloudIDMarker;
    private Marker                    detailMarker;
    private Marker                    mlastMarker;
    private RelativeLayout            mPoiDetail;
    private TextView                  mPoiName, mPoiAddress, mPoiDistance;
    private View globalView;
    private LinearLayout jumpLayout;
    private int seachScape;
    private int seachNum;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FloatingActionMenu   bottomMenu;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;

    private Handler mUiHandler = new Handler();

    private List<FloatingActionMenu> menus = new ArrayList<>();

    private String               TAG     = "CityRecall";
    private ArrayList<CloudItem> items   = new ArrayList<CloudItem>();
    private String               tableId = ServerParameter.CLOUDMAP_DIY_TABLEID;

    private String  keyWord            = ServerParameter.CLOUD_SERVICE_KEYWORD;
    private boolean isInitNearbySearch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        globalView = inflater.inflate(R.layout.activity_homepage, container, false);
        mapView = (MapView) globalView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();
        initBottomMenu();
        initSetting();
        UserUtils.initCloudService(getActivity());

        return globalView;
    }

    private void initBottomMenu(){
        bottomMenu = (FloatingActionMenu) globalView.findViewById(R.id.bottom_menu);
        fab1 = (FloatingActionButton) globalView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) globalView.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) globalView.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) globalView.findViewById(R.id.fab4);
        bottomMenu.setClosedOnTouchOutside(true);
        bottomMenu.hideMenuButton(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menus.add(bottomMenu);

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);

        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomMenu.showMenuButton(true);
            }
        }, 400);

        bottomMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMenu.toggle(true);
            }
        });
    }

    private void initSetting(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        seachScape = sharedPreferences.getInt("seachScape", 3000);
        seachNum = sharedPreferences.getInt("seachNum", 10);
    }

    private void initMap(){
        if (aMap == null) {
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
            mPoiDetail = (RelativeLayout) globalView.findViewById(R.id.poi_detail);
            mPoiName = (TextView) globalView.findViewById(R.id.poi_name);
            mPoiAddress = (TextView) globalView.findViewById(R.id.poi_address);
            mPoiDistance = (TextView) globalView.findViewById(R.id.poi_distance);
            jumpLayout = (LinearLayout) globalView.findViewById(R.id.jump_layout);
            jumpLayout.setOnClickListener(this);

            cloudSearch = new CloudSearch(getContext());
            cloudSearch.setOnCloudSearchListener(this);
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("\n正在搜索   ");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void searchByBound() {
        showProgressDialog();
        items.clear();
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(
                lp.getLatitude(), lp.getLongitude()), seachScape);
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
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        whetherToShowDetailInfo(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);
                longtitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();
                if (!isInitNearbySearch) {
                    lp = new LatLonPoint(latitude, longtitude);
                    searchByBound();
                    isInitNearbySearch = true;
                }
            } else {
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
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        aMap.clear();
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getContext());
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
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_layout:
                Intent intent = new Intent(getActivity(), StatusDetailActivity.class);
                intent.putExtra("detailObject", (Parcelable) mlastMarker.getObject());
                startActivity(intent);
                break;
            case R.id.fab1:
                getActivity().finish();
                Intent refreshIntent = new Intent(getActivity(), getActivity().getClass());
                startActivity(refreshIntent);
                break;
            case R.id.fab2:
                Intent intent_fab2 = new Intent(getActivity(), ReleaseRecall.class);
                startActivity(intent_fab2);
                break;
            case R.id.fab3:
                Intent hotStatusIntent = new Intent(getActivity(), HotStatusActivity.class);
                hotStatusIntent.putExtra("position", (Parcelable) lp);
                startActivity(hotStatusIntent);
                break;
            case R.id.fab4:
                Intent selectPositionIntent = new Intent(getActivity(), SelectPostionActivity.class);
                selectPositionIntent.putExtra("flag", 1);
//                startActivity(selectPositionIntent);
                startActivityForResult(selectPositionIntent, 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCloudSearched(CloudResult result, int code) {
        dismissProgressDialog();
        if (code == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {
                    mCloudItems = result.getClouds();
                    if (mCloudItems != null && mCloudItems.size() > 0) {
                        cloudOverlay = new CloudOverlay(aMap, mCloudItems, getResources());
                        cloudOverlay.removeFromMap();
                        cloudOverlay.addToMap();
                        for (CloudItem item : mCloudItems) {
                            items.add(item);
                        }
                        if (query.getBound().getShape().equals(CloudSearch.SearchBound.BOUND_SHAPE)) {
                            aMap.addCircle(new CircleOptions().center(new LatLng(lp.getLatitude(), lp.getLongitude()))
                                    .radius(seachScape).strokeColor(Color.BLACK)
                                    .strokeWidth(3));
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lp.getLatitude(),
                                            lp.getLongitude()), 12));
                        }
                    } else {
                        ToastUtils.showToast(getActivity(), "无结果", Toast.LENGTH_SHORT);
                    }
                }
            } else {
                ToastUtils.showToast(getActivity(), "无结果", Toast.LENGTH_SHORT);
            }
        } else {
            ToastUtils.showToast(getActivity(), "无结果", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int code) {
        dismissProgressDialog();
        if (code == 1000 && item != null) {
            if (mCloudIDMarker != null) {
                mCloudIDMarker.destroy();
            }
            LatLng position = AMapUtil.convertToLatLng(item.getLatLonPoint());
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(position, 18, 0, 30)));
            mCloudIDMarker = aMap.addMarker(new MarkerOptions().position(position)
                    .title(item.getTitle())
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding))));
            items.add(item);
        } else {
            ToastUtils.showToast(getActivity(), code + "", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String tile = marker.getTitle();
        for (CloudItem item : items) {
            if (tile.equals(item.getTitle())) {
                Intent intent = new Intent(getActivity(),
                        HomeActivity.class);
                intent.putExtra("clouditem", item);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetLastMarker();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                CloudItem mCurrentPoi = (CloudItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    resetLastMarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));
                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                //Todo handle exception
                e.printStackTrace();
            }
        } else {
            whetherToShowDetailInfo(false);
            resetLastMarker();
        }
        return true;
    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
        } else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }

    private void setPoiItemDisplayContent(final CloudItem mCurrentPoi) {
        //distance
        mPoiDistance.setText("距离你" + mCurrentPoi.getDistance() + "米");

        //username and content
        Iterator iterator = mCurrentPoi.getCustomfield().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            //Todo favour parameter
            if (key.equals("content")) {
                mPoiAddress.setText(val.toString());
            } else if (key.equals("username")) {
                mPoiName.setText(val.toString());
            }
        }
    }

    private void resetLastMarker() {
        mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding)));
        mlastMarker = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    LatLng latLng = data.getParcelableExtra("position");
                    L.d("position"+latLng.latitude + ":"+latLng.longitude);
                    lp = new LatLonPoint(latLng.latitude, latLng.longitude);
                    searchByBound();
                    aMap.clear();
                }

        }
    }
}

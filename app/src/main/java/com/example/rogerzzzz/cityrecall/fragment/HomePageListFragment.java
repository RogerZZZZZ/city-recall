package com.example.rogerzzzz.cityrecall.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.utils.AMapUtil;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.CloudOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rogerzzzz on 16/5/6.
 */
public class HomePageListFragment extends Fragment implements LocationSource, AMapLocationListener,
        AMap.InfoWindowAdapter, CloudSearch.OnCloudSearchListener, View.OnClickListener{
    private MaterialListView materialListView;
    private List<Card> mList;

    public  double                                   longtitude;
    public  double                                   latitude;
    private MapView                                  mapView;
    private AMap                                     aMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient                       mLocationClient;
    private AMapLocationClientOption                 mLocationOption;
    private LatLonPoint                              lp;
    private CloudSearch                              cloudSearch;
    private CloudSearch.Query                        query;
    private CloudOverlay                             cloudOverlay;
    private List<CloudItem>                          mCloudItems;
    private ProgressDialog                           progressDialog;
    private Marker                                   mCloudIDMarker;
    private View         globalView;

    private ArrayList<CloudItem> items   = new ArrayList<CloudItem>();
    private String               tableId = ServerParameter.CLOUDMAP_DIY_TABLEID;

    private String  keyWord            = ServerParameter.CLOUD_SERVICE_KEYWORD;
    private boolean isInitNearbySearch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        globalView = inflater.inflate(R.layout.fragment_homepage_list, container, false);
        mapView = (MapView) globalView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();
        UserUtils.initCloudService(getActivity());

        return globalView;
    }

    private void initMap(){
        cloudSearch = new CloudSearch(getContext());
        cloudSearch.setOnCloudSearchListener(this);
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
                lp.getLatitude(), lp.getLongitude()), 4000);
        try {
            query = new CloudSearch.Query(tableId, keyWord, bound);
            //Todo size
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
        deactivate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("location", "starts");
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);
                longtitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();
                Log.d("location", latitude + ":" + longtitude);
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
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        Log.d("activate", "start");
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

    }

    @Override
    public void onCloudSearched(CloudResult result, int code) {
        dismissProgressDialog();
        if (code == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {
                    mCloudItems = result.getClouds();
                    Log.d("size", result.getClouds().size() + "");
                    if (mCloudItems != null && mCloudItems.size() > 0) {
                        cloudOverlay = new CloudOverlay(aMap, mCloudItems, getResources());
                        cloudOverlay.removeFromMap();
                        cloudOverlay.addToMap();
                        for (CloudItem item : mCloudItems) {
                            items.add(item);
                        }
                        if (query.getBound().getShape().equals(CloudSearch.SearchBound.BOUND_SHAPE)) {
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
//            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(position, 18, 0, 30)));
//            mCloudIDMarker = aMap.addMarker(new MarkerOptions().position(position)
//                    .title(item.getTitle())
//                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_gcoding))));
            items.add(item);
        } else {
            ToastUtils.showToast(getActivity(), code + "", Toast.LENGTH_SHORT);
        }
    }

}

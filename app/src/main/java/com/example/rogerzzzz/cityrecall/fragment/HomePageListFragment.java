package com.example.rogerzzzz.cityrecall.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.StatusDetailActivity;
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.CloudOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by rogerzzzz on 16/5/6.
 */
public class HomePageListFragment extends Fragment implements LocationSource, AMapLocationListener,
        AMap.InfoWindowAdapter, CloudSearch.OnCloudSearchListener{

    public  double                    longtitude;
    public  double                    latitude;
    private MapView                   mapView;
    private AMap                      aMap;
    private MaterialListView materialListView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient        mLocationClient;
    private AMapLocationClientOption  mLocationOption;
    private LatLonPoint               lp;
    private CloudSearch               cloudSearch;
    private CloudSearch.Query         query;
    private CloudOverlay              cloudOverlay;
    private List<CloudItem>           mCloudItems;
    private ProgressDialog            progressDialog;
    private View globalView;
    private List<Card> cards = new ArrayList<>();

    private String               TAG     = "CityRecall";
    private ArrayList<CloudItem> items   = new ArrayList<CloudItem>();
    private String               tableId = ServerParameter.CLOUDMAP_DIY_TABLEID;

    private String  keyWord            = ServerParameter.CLOUD_SERVICE_KEYWORD;
    private boolean isInitNearbySearch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        globalView = inflater.inflate(R.layout.fragment_homepage_list, container, false);
        mapView = (MapView) globalView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        materialListView = (MaterialListView) globalView.findViewById(R.id.materail_listview);
        initMap();
        initMaterailList();
        UserUtils.initCloudService(getActivity());

        return globalView;
    }

    private void initMaterailList(){
        materialListView.setItemAnimator(new SlideInLeftAnimator());
        materialListView.getItemAnimator().setAddDuration(300);
        materialListView.getItemAnimator().setRemoveDuration(300);

        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(@NonNull Card card, int position) {
                cloudSearch.searchCloudDetailAsyn(tableId, card.getTag() + "");
            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {

            }
        });
    }

    private void initMap(){
        if (aMap == null) {
            aMap = mapView.getMap();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
            aMap.setInfoWindowAdapter(this);

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
                    Log.d("location", latitude + ": " + longtitude);
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
                        initCardView();
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

    private void initCardView(){
        for(CloudItem item : mCloudItems){
            String id = item.getID();
            String username = "", content = "";
            Iterator iterator = item.getCustomfield().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                if (key.equals("content")) {
                    content = val.toString();
                } else if (key.equals("username")) {
                    username = val.toString();
                }
            }

            AVQuery<AVObject> query_pic = new AVQuery<AVObject>("ReCall");
            query_pic.whereEqualTo("mapItemId", id);
            final String finalUsername = username;
            final String finalContent = content;
            final String finalMapId = id;
            query_pic.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> avObjects, AVException e) {
                    if (e == null && avObjects.size() > 0) {
                        String picWallUrl = avObjects.get(0).get("picString").toString();
                        List<String> list = Arrays.asList(picWallUrl.split(","));
                        final String finalPicWallUrl = picWallUrl;
                        final List<String> finalList = list;

                        AVQuery<AVObject> favourQuery = new AVQuery<AVObject>("Favour");
                        favourQuery.whereEqualTo("statusId", finalMapId);
                        favourQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int i, AVException e) {
                                Log.d("favoruNUm", i+"");
                                //with pics
                                if(!finalPicWallUrl.equals("")){
                                    String picString = finalList.get(0);
                                    materialListView.getAdapter().add(getCardItem(1, finalUsername, finalContent, picString, finalMapId, i+""));
                                }else if(false){
                                    //Todo record
                                }else{
                                    //with only words
                                    materialListView.getAdapter().add(getCardItem(0, finalUsername, finalContent, "", finalMapId, i+""));
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /*
    * param
    * int type 0: Content with only words.
    *          1: Content with words and pics.
    *          2: Content with words and record.
     */
    private Card getCardItem(int type, String username, String content, String picString, String mapItemId, String favourNum){
        switch (type){
            case 0:{
                final CardProvider provider = new Card.Builder(getContext())
                        .setTag(mapItemId)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_basic_buttons_card)
                        .setTitle(username)
                        .setDescription(content)
                        .addAction(R.id.favourNum, new TextViewAction(getContext())
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Toast.makeText(getContext(), "You have pressed the left button", Toast.LENGTH_SHORT).show();
                                    }
                                }));
                provider.setFavourNum(favourNum);
                return provider.endConfig().build();
            }
            case 1:{
                final CardProvider provider = new Card.Builder(getContext())
                        .setTag(mapItemId)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_image_with_buttons_card)
                        .setTitle(username)
                        .setDescription(content)
                        .setDrawable(picString)
                        .addAction(R.id.favourNum, new TextViewAction(getContext())
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Toast.makeText(getContext(), "You have pressed the right button", Toast.LENGTH_SHORT).show();
                                    }
                                }));

                provider.setFavourNum(favourNum);

                return provider.endConfig().build();
            }
            default:{
                return null;
            }
        }
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int code) {
        if (code == 1000 && item != null) {
            Intent intent = new Intent(getActivity(), StatusDetailActivity.class);
            intent.putExtra("detailObject", (Parcelable) item);
            startActivity(intent) ;
        } else {
            ToastUtils.showToast(getActivity(), code + "", Toast.LENGTH_SHORT);
        }
    }
}

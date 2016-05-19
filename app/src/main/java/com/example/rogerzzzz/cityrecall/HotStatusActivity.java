package com.example.rogerzzzz.cityrecall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
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
import com.avos.avoscloud.FindCallback;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.utils.StringUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.CloudOverlay;
import com.example.rogerzzzz.cityrecall.widget.UPlayer;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by rogerzzzz on 16/5/16.
 */
public class HotStatusActivity extends AppCompatActivity implements CloudSearch.OnCloudSearchListener, LocationSource{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public  double                    longtitude;
    public  double                    latitude;
    private MapView                   mapView;
    private AMap                      aMap;
    private MaterialListView          materialListView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient        mLocationClient;
    private AMapLocationClientOption  mLocationOption;
    private LatLonPoint               lp;
    private CloudSearch               cloudSearch;
    private CloudSearch.Query         query;
    private CloudOverlay              cloudOverlay;
    private List<CloudItem>           mCloudItems;
    private ProgressDialog            progressDialog;
    private List<Card> cards = new ArrayList<>();
    private int               seachScape;
    private int               seachNum;
    private SharedPreferences sharedPreferences;

    private LinearLayout       rootLayout;

    private List<FloatingActionMenu> menus = new ArrayList<>();

    private String               TAG     = "CityRecall";
    private ArrayList<CloudItem> items   = new ArrayList<CloudItem>();
    private String               tableId = ServerParameter.CLOUDMAP_DIY_TABLEID;

    private String  keyWord            = ServerParameter.CLOUD_SERVICE_KEYWORD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotstatus);
        ButterKnife.bind(this);
        toolbar.setTitle("热门状态");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UserUtils.initCloudService(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        materialListView = (MaterialListView) findViewById(R.id.materail_listview);
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        initSetting();
        initMap();
        initSearchPosition();
        initMaterailList();
    }

    private void initSearchPosition(){
        lp = getIntent().getParcelableExtra("position");
        searchByBound();
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

    private void initSetting(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        seachScape = sharedPreferences.getInt("seachScape", 3000);
        seachNum = sharedPreferences.getInt("seachNum", 10);
    }

    private void initMap(){
        if (aMap == null) {
            aMap = mapView.getMap();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
//            aMap.setInfoWindowAdapter(this);

            cloudSearch = new CloudSearch(this);
            cloudSearch.setOnCloudSearchListener(this);
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) progressDialog = new ProgressDialog(this);
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
                        Snackbar.make(rootLayout, "无结果", Snackbar.LENGTH_SHORT).show();
                    }
                }
            } else {
                Snackbar.make(rootLayout, "无结果", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(rootLayout, "无结果", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int code) {
        if (code == 1000 && item != null) {
            Intent intent = new Intent(HotStatusActivity.this, StatusDetailActivity.class);
            intent.putExtra("detailObject", (Parcelable) item);
            startActivity(intent) ;
        } else {
            Snackbar.make(rootLayout, "查询失败", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        aMap.clear();
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
//            mLocationClient.setLocationListener(this);
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

    private void initCardView(){
        List<String> idArray = new ArrayList<>();
        final HashMap<String, Integer> map = new HashMap<>();
        for(CloudItem item : mCloudItems){
            idArray.add(item.getID());
            map.put(item.getID(), item.getDistance());
        }

        AVQuery<AVObject> query = AVQuery.getQuery("ReCall");
        query.whereContainedIn("mapItemId", idArray);
        query.orderByDescending("favourCount");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null){
                    for(AVObject avObject : list){
                        String username = avObject.get("username").toString();
                        String content = avObject.get("content").toString();
                        String mapId = avObject.get("mapItemId").toString();
                        String picWallUrl = avObject.get("picString").toString();
                        String recordUrl = avObject.get("radioPath").toString();
                        String favourCount = avObject.get("favourCount").toString();
                        List<String> picList = Arrays.asList(picWallUrl.split(","));
                        String finalPicWallUrl = picWallUrl;
                        List<String> finalList = picList;
                        String finalRecordUrl = recordUrl;
                        if(!finalPicWallUrl.equals("")){
                            String picString = finalList.get(0);
                            materialListView.getAdapter().add(getCardItem(1, username, content, picString, mapId, finalRecordUrl, map.get(mapId), favourCount));
                        }else if(!finalRecordUrl.equals("") && finalRecordUrl != null){
                            //Todo record
                            materialListView.getAdapter().add(getCardItem(2, username, content, "", mapId, finalRecordUrl, map.get(mapId), favourCount));
                        }else{
                            //with only words
                            materialListView.getAdapter().add(getCardItem(0, username, content, "", mapId, finalRecordUrl, map.get(mapId), favourCount));
                        }
                    }
                }
            }
        });
    }

    /*
    * param
    * int type 0: Content with only words.
    *          1: Content with words and pics.
    *          2: Content with words and record.
     */
    private Card getCardItem(int type, String username, String content, String picString, String mapItemId, final String recordUrl, int distance, String favourNum){
        SpannableString weiboContent = StringUtils.getWeiboContent(this, null, content);
        switch (type){
            case 0:{
                final CardProvider provider = new Card.Builder(this)
                        .setTag(mapItemId)
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_basic_buttons_card)
                        .setTitle(username)
                        .setDescription(weiboContent);
                provider.setFavourNum(favourNum);
                provider.setDistance("距离你" + distance + "米");
                return provider.endConfig().build();
            }
            case 1:{
                final CardProvider provider = new Card.Builder(this)
                        .setTag(mapItemId)
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_image_with_buttons_card)
                        .setTitle(username)
                        .setDescription(weiboContent)
                        .setDrawable(picString);

                provider.setFavourNum(favourNum);
                provider.setDistance("距离你" + distance + "米");
                return provider.endConfig().build();
            }
            case 2: {
                final CardProvider provider = new Card.Builder(this)
                        .setTag(mapItemId)
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_basic_case2)
                        .setTitle(username)
                        .setDescription(weiboContent)
                        .addAction(R.id.left_text_button, new TextViewAction(this)
                                .setText("播放")
                                .setTextResourceColor(R.color.black_button)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        UPlayer uPlayer = new UPlayer(recordUrl);
                                        uPlayer.start();
                                    }
                                }));
                provider.setFavourNum(favourNum);
                provider.setDistance("距离你" + distance + "米");
                return provider.endConfig().build();
            }
            default:{
                return null;
            }
        }
    }
}

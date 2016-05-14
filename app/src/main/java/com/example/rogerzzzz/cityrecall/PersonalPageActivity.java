package com.example.rogerzzzz.cityrecall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
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
import com.example.rogerzzzz.cityrecall.enity.ServerParameter;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.HeaderView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by rogerzzzz on 16/5/12.
 */
public class PersonalPageActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, CloudSearch.OnCloudSearchListener{
    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.materail_listview)
    MaterialListView materialListView;

    private boolean isHideToolbarView = false;
    private String username;
    private CloudSearch cloudSearch;
    private String               tableId = ServerParameter.CLOUDMAP_DIY_TABLEID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalpage);
        ButterKnife.bind(this);
        UserUtils.initCloudService(this);

        username = getIntent().getStringExtra("username");
        cloudSearch = new CloudSearch(this);
        cloudSearch.setOnCloudSearchListener(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initUi();
        initMaterailList();
        initCardView();
    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);

        toolbarHeaderView.bindTo(username, "Last seen today at 7.00PM");
        floatHeaderView.bindTo(username, "Last seen today at 7.00PM");
        ImageView imageView = (ImageView) findViewById(R.id.image);

        Picasso.with(PersonalPageActivity.this).load(R.drawable.person_bg).into(imageView);
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

    private void initCardView(){
        AVQuery<AVObject> query = new AVQuery<>("ReCall");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if(e == null && avObjects.size() > 0){
                    for(AVObject object : avObjects){
                        String picWallUrl = object.get("picString").toString();
                        List<String> list = Arrays.asList(picWallUrl.split(","));
                        String mapId = object.get("mapItemId").toString();
                        String content = object.get("content").toString();
                        final String finalPicWallUrl = picWallUrl;
                        final List<String> finalList = list;
                        final String finalMapId = mapId;
                        final String finalContent = content;

                        AVQuery<AVObject> favourQuery = new AVQuery<AVObject>("Favour");
                        favourQuery.whereEqualTo("statusId", finalMapId);
                        favourQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int i, AVException e) {
                                //with pics
                                if(!finalPicWallUrl.equals("")){
                                    String picString = finalList.get(0);
                                    materialListView.getAdapter().add(getCardItem(1, username, finalContent, picString, finalMapId, i+""));
                                }else if(false){
                                    //Todo record
                                }else{
                                    //with only words
                                    materialListView.getAdapter().add(getCardItem(0, username, finalContent, "", finalMapId, i+""));
                                }
                            }
                        });
                    }
                }else{
                    e.printStackTrace();
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
    private Card getCardItem(int type, String username, String content, String picString, String mapItemId, String favourNum){
        switch (type){
            case 0:{
                final CardProvider provider = new Card.Builder(this)
                        .setTag(mapItemId)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_basic_buttons_card)
                        .setTitle(username)
                        .setDescription(content)
                        .addAction(R.id.favourNum, new TextViewAction(this)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
//                                        Toast.makeText(getContext(), "You have pressed the left button", Toast.LENGTH_SHORT).show();
                                    }
                                }));
                provider.setFavourNum(favourNum);
                return provider.endConfig().build();
            }
            case 1:{
                final CardProvider provider = new Card.Builder(this)
                        .setTag(mapItemId)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_image_with_buttons_card)
                        .setTitle(username)
                        .setDescription(content)
                        .setDrawable(picString)
                        .addAction(R.id.favourNum, new TextViewAction(this)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
//                                        Toast.makeText(getContext(), "You have pressed the right button", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
            toolbar.setBackgroundColor(Color.parseColor("#ff8200"));

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
            toolbar.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onCloudSearched(CloudResult cloudResult, int i) {

    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int code) {
        if (code == 1000 && item != null) {
            Intent intent = new Intent(PersonalPageActivity.this, StatusDetailActivity.class);
            intent.putExtra("detailObject", (Parcelable) item);
            startActivity(intent) ;
        } else {
            ToastUtils.showToast(PersonalPageActivity.this, code + "", Toast.LENGTH_SHORT);
        }
    }
}

package com.example.rogerzzzz.cityrecall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
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
import com.example.rogerzzzz.cityrecall.widget.UPlayer;
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

    @Bind(R.id.rootLayout)
    RelativeLayout rootLayout;

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
        initFollowFunction();
        initMaterailList();
        initCardView();
        userInfo();
    }

    private void initFollowFunction(){
        if(!username.equals(AVUser.getCurrentUser().getUsername())){
            //initialize
            AVQuery<AVUser> userQuery = new AVQuery<>("_User");
            userQuery.whereEqualTo("username", username);
            userQuery.findInBackground(new FindCallback<AVUser>() {
                @Override
                public void done(List<AVUser> list, AVException e) {
                    if(e == null && list.size() > 0){
                        final String currentUserObjectId = list.get(0).getObjectId();
                        AVUser pageUser = list.get(0);
                        AVQuery<AVUser> followerNameQuery = pageUser.followerQuery(pageUser.getObjectId(), AVUser.class);
                        followerNameQuery.whereEqualTo("follower", AVUser.getCurrentUser());
                        followerNameQuery.findInBackground(new FindCallback<AVUser>() {
                            @Override
                            public void done(List<AVUser> list, AVException e) {
                                if(e == null && list.size() > 0){
                                    toolbarHeaderView.setFollowActionStatus(true);
                                }else if(e == null && list.size() == 0){
                                    toolbarHeaderView.setFollowActionStatus(false);
                                }

                                //bind clicker
                                toolbarHeaderView.bindFollowAction(new HeaderView.followAction() {
                                    @Override
                                    public void followAction(final TextView tv, final TextView un_tv) {
                                        tv.setVisibility(View.GONE);
                                        un_tv.setVisibility(View.VISIBLE);
                                        Snackbar.make(rootLayout, "已经关注该用户", Snackbar.LENGTH_SHORT).show();
                                        AVUser.getCurrentUser().followInBackground(currentUserObjectId, null, new FollowCallback() {
                                            @Override
                                            public void done(AVObject avObject, AVException e) {
                                                if(e == null){
                                                    tv.setVisibility(View.GONE);
                                                    un_tv.setVisibility(View.VISIBLE);
                                                } else if(e.getCode() == AVException.DUPLICATE_VALUE){
                                                    Snackbar.make(rootLayout, "已经关注过了", Snackbar.LENGTH_SHORT).show();
                                                }else{
                                                    Snackbar.make(rootLayout, "关注失败，稍后再试", Snackbar.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            protected void internalDone0(Object o, AVException e) {
                                            }
                                        });
                                    }
                                });

                                toolbarHeaderView.bindUnfollowAction(new HeaderView.unFollowAction() {
                                    @Override
                                    public void unFollowAction(final TextView tv, final TextView un_tv) {
                                        tv.setVisibility(View.VISIBLE);
                                        un_tv.setVisibility(View.GONE);
                                        Snackbar.make(rootLayout, "已经取消对该用户的关注", Snackbar.LENGTH_SHORT).show();
                                        AVUser.getCurrentUser().unfollowInBackground(currentUserObjectId, new FollowCallback() {
                                            @Override
                                            public void done(AVObject avObject, AVException e) {
                                                if(e == null){
                                                    tv.setVisibility(View.GONE);
                                                    un_tv.setVisibility(View.VISIBLE);
                                                }else{
                                                    Snackbar.make(rootLayout, "关注失败，稍后再试", Snackbar.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            protected void internalDone0(Object o, AVException e) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        Picasso.with(PersonalPageActivity.this).load(R.drawable.person_bg).into(imageView);
    }

    private void userInfo(){
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null){
                    if(list.get(0).get("description") != null){
                        String description = list.get(0).get("description").toString();
                        toolbarHeaderView.bindTo(username, description);
                        floatHeaderView.bindTo(username, description);
                    }else{
                        toolbarHeaderView.bindTo(username, "");
                        floatHeaderView.bindTo(username, "");
                    }
                }
            }
        });

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
                        String recordUrl = object.get("radioPath").toString();
                        List<String> list = Arrays.asList(picWallUrl.split(","));
                        String mapId = object.get("mapItemId").toString();
                        String content = object.get("content").toString();
                        final String finalPicWallUrl = picWallUrl;
                        final List<String> finalList = list;
                        final String finalMapId = mapId;
                        final String finalContent = content;
                        final String finalRecordUrl = recordUrl;

                        AVQuery<AVObject> favourQuery = new AVQuery<AVObject>("Favour");
                        favourQuery.whereEqualTo("statusId", finalMapId);
                        favourQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int i, AVException e) {
                                //with pics
                                if(!finalPicWallUrl.equals("")){
                                    String picString = finalList.get(0);
                                    materialListView.getAdapter().add(getCardItem(1, username, finalContent, picString, finalMapId, finalRecordUrl, i+""));
                                }else if(!finalRecordUrl.equals("") && finalRecordUrl != null){
                                    materialListView.getAdapter().add(getCardItem(2, username, finalContent, "", finalMapId, finalRecordUrl, i+""));
                                    //Todo record
                                }else{
                                    //with only words
                                    materialListView.getAdapter().add(getCardItem(0, username, finalContent, "", finalMapId, finalRecordUrl, i+""));
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
    private Card getCardItem(int type, String username, String content, String picString, String mapItemId, final String recordUrl, String favourNum){
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
            case 2: {
                final CardProvider provider = new Card.Builder(this)
                        .setTag(mapItemId)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_basic_case2)
                        .setTitle(username)
                        .setDescription(content)
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

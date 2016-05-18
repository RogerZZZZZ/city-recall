package com.example.rogerzzzz.cityrecall.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rogerzzzz.cityrecall.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by anton on 11/12/15.
 */

public class HeaderView extends LinearLayout implements View.OnClickListener{

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.last_seen)
    TextView lastSeen;

    @Bind(R.id.followBtn_tv)
    TextView followBtn;

    @Bind(R.id.unfollowBtn_tv)
    TextView unFollowBtn;

    private followAction followAction;
    private unFollowAction unFollowAction;

    public static interface followAction{
        void followAction(TextView tv, TextView un_tv);
    }

    public static interface unFollowAction{
        void unFollowAction(TextView tv, TextView un_tv);
    }

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(String name, String lastSeen) {
        this.name.setText(name);
        this.lastSeen.setText(lastSeen);
    }

    /**
     * @param isFollowing
     * true : 正在关注
     * false : 没有关注
     */
    public void setFollowActionStatus(boolean isFollowing){
        if(isFollowing){
            this.followBtn.setVisibility(View.GONE);
            this.unFollowBtn.setVisibility(View.VISIBLE);
        }else{
            this.followBtn.setVisibility(View.VISIBLE);
            this.unFollowBtn.setVisibility(View.GONE);
        }
        this.followBtn.setOnClickListener(this);
        this.unFollowBtn.setOnClickListener(this);
    }

    public void bindFollowAction(followAction followAction){
        this.followAction = followAction;
    }

    public void bindUnfollowAction(unFollowAction unFollowAction){
        this.unFollowAction = unFollowAction;
    }

    public void setTextSize(float size) {
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.followBtn_tv:
                if(followAction != null){
                    followAction.followAction(followBtn, unFollowBtn);
                }
                break;
            case R.id.unfollowBtn_tv:
                if(unFollowAction != null){
                    unFollowAction.unFollowAction(followBtn, unFollowBtn);
                }
                break;
            default:
                break;
        }
    }
}

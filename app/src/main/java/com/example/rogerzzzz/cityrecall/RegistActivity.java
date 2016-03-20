package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.Window;

import com.example.rogerzzzz.cityrecall.fragment.RegistTab;
import com.example.rogerzzzz.cityrecall.fragment.RegistTabOptional;
import com.example.rogerzzzz.cityrecall.utils.TitleBuilder;
import com.example.rogerzzzz.cityrecall.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rogerzzzz on 16/3/20.
 */
public class RegistActivity extends FragmentActivity implements View.OnClickListener{
    private NoScrollViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_regist);

        initView();
    }

    private void initView(){
        new TitleBuilder(this)
                .setLeftText("返回")
                .setLeftOnClickListener(this)
                .setTitleText("注册");

        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);

        RegistTab tab = new RegistTab();
        RegistTabOptional tabOptional = new RegistTabOptional();
        mFragment.add(tab);
        mFragment.add(tabOptional);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.titlebar_tv_left:
                finish();
//                viewPager.setCurrentItem(1);
        }

    }
}

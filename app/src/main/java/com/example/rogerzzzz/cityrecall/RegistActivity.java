package com.example.rogerzzzz.cityrecall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.rogerzzzz.cityrecall.fragment.RegistTab;
import com.example.rogerzzzz.cityrecall.fragment.RegistTabOptional;
import com.example.rogerzzzz.cityrecall.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/3/20.
 */
public class RegistActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewpager)
    NoScrollViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment> mFragment = new ArrayList<Fragment>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        RegistTab tab = new RegistTab();
        RegistTabOptional tabOptional = new RegistTabOptional();
        mFragment.add(tab);
        mFragment.add(tabOptional);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        switch (view.getId()) {
            case R.id.titlebar_tv_left:
                finish();
        }

    }

}

package com.example.rogerzzzz.cityrecall;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.rogerzzzz.cityrecall.adapter.ContentFragmentAdapter;
import com.example.rogerzzzz.cityrecall.fragment.HomePageListFragment;
import com.example.rogerzzzz.cityrecall.fragment.HomePageMapFragment;
import com.example.rogerzzzz.cityrecall.utils.BitmapHelper;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private ImageView potrait_iv;
    private MyTask       task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpNavigationDrawer();
        initTabs();

        // Initial tab count
        setTabs(2);
    }

    private void initTabs(){
        HomePageMapFragment mapFragment = new HomePageMapFragment();
        HomePageListFragment listFragment = new HomePageListFragment();
        mFragment.add(listFragment);
        mFragment.add(mapFragment);
    }

    private void setUpNavigationDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("城市记忆");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        } catch (Exception ignored) {
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headView = mNavigationView.getHeaderView(0);
        TextView username_tv = (TextView) headView.findViewById(R.id.username_tv);
        potrait_iv = (ImageView) headView.findViewById(R.id.menu_item_img);

        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("username", AVUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException e) {
                if (e == null) {
                    AVFile avFile = avObjects.get(0).getAVFile("avatar");
                    final String url = avFile.getUrl();
                    task = new MyTask(url);
                    task.execute();
                } else {
                    e.printStackTrace();
                }
            }
        });

        username_tv.setText(AVUser.getCurrentUser().getUsername());
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.detail_icon:
                        break;
                    case R.id.detailcollect_icon:
                        break;
                    case R.id.setting_icon:
                        break;
                    case R.id.write_icon:
                        Intent intentWrite = new Intent(HomeActivity.this, ReleaseRecall.class);
                        startActivity(intentWrite);
                        break;
                    case R.id.logout_icon:
                        UserUtils.userLogout();
                        Intent intentLogout = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intentLogout);
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawer(mNavigationView);
                return true;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    public void setTabs(int count) {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        ContentFragmentAdapter adapterViewPager = new ContentFragmentAdapter(getSupportFragmentManager(), this, count, mFragment);
        vpPager.setAdapter(adapterViewPager);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
        slidingTabLayout.setTextColorSelected(getResources().getColor(R.color.tab_text_color_selected));
        slidingTabLayout.setDistributeEvenly();
        slidingTabLayout.setViewPager(vpPager);
        slidingTabLayout.setTabSelected(0);

        // Change indicator color
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tab_indicator);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    Toast.makeText(HomeActivity.this, query, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView);
            } else {
                mDrawerLayout.openDrawer(mNavigationView);
            }
            return true;
        }

        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        private String url = "";
        private Bitmap bitmap;

        public MyTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... strings) {
            bitmap = BitmapHelper.getBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            potrait_iv.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(false);
        }
    }
}

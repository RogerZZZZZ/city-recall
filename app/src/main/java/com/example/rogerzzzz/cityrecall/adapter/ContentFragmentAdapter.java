package com.example.rogerzzzz.cityrecall.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Copyright (C) 2015 Mustafa Ozcan
 * Created on 06 May 2015 (www.mustafaozcan.net)
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * *
 * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ContentFragmentAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 1;
    private final Context c;
    private List<Fragment> tabs;
    private String[] tabName = new String[]{"列表模式", "地图模式"};

    public ContentFragmentAdapter(FragmentManager fragmentManager, Context context, int item_count, List<Fragment> tabs) {
        super(fragmentManager);
        NUM_ITEMS = item_count;
        this.tabs = tabs;
        c = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return tabs.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tabName[position];
    }

}

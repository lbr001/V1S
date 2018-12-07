package com.sunmi.helper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lbr
 * 功能描述:fragment适配adapter
 * 创建时间: 2018-10-25 14:17
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    //fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mFragmentList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}

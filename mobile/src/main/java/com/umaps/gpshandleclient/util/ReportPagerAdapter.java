package com.umaps.gpshandleclient.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by beou on 01/06/2015.
 */
public class ReportPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;
    public ReportPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public ReportPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

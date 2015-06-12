package com.umaps.gpshandleclient.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.util.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class AdminPager extends Fragment {
    public static AdminPager newInstance(){
        return new AdminPager();
    }
    public AdminPager(){
        super();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_pager_admin, container, false);
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.view_pager_admin);
        List<Fragment> fragments = getFragments();
        PagerAdapter mPageAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mPageAdapter);
        return view;
    }
    private List<Fragment> getFragments() {
        List<Fragment> frags = new ArrayList<>();
        AdmAccount admAccount = AdmAccount.newInstance();
        AdmDeviceGroup admDeviceGroup = AdmDeviceGroup.newInstance();
        frags.add(admAccount);
        frags.add(admDeviceGroup);
        return frags;
    }
}

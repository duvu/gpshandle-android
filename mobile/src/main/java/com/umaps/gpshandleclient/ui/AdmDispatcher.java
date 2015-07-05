package com.umaps.gpshandleclient.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.util.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class AdmDispatcher extends Fragment {
    private static final String TAG = "AdminDispatcher";
    public static AdmDispatcher newInstance(){
        return new AdmDispatcher();
    }
    public AdmDispatcher(){
        super();
    }
    MyApplication mApplication;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_pager_admin, container, false);
        final ViewPager mViewPager = (ViewPager) view.findViewById(R.id.view_pager_admin);
        mApplication = MyApplication.getInstance();

        List<Fragment> fragments = getFragments();
        PagerAdapter mPageAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mPageAdapter);




        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            View aV = view.findViewById(R.id.selected_account);
            View uV = view.findViewById(R.id.selected_user);
            View gV = view.findViewById(R.id.selected_group);
            View dV = view.findViewById(R.id.selected_device);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        aV.setBackgroundColor(getResources().getColor(R.color.selected));
                        uV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        gV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        dV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        break;
                    case 1:
                        aV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        uV.setBackgroundColor(getResources().getColor(R.color.selected));
                        gV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        dV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        break;
                    case 2:
                        aV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        uV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        gV.setBackgroundColor(getResources().getColor(R.color.selected));
                        dV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        break;
                    case 3:
                        aV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        uV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        gV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        dV.setBackgroundColor(getResources().getColor(R.color.selected));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        View lA = view.findViewById(R.id.l_account);
        View lU = view.findViewById(R.id.l_user);
        View lG = view.findViewById(R.id.l_group);
        View lD = view.findViewById(R.id.l_device);

        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, true);
            }
        });
        lU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, true);
            }
        });

        lG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2, true);
            }
        });
        lD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(3, true);
            }
        });
        return view;
    }
    private List<Fragment> getFragments() {
        List<Fragment> frags = new ArrayList<>();
        Log.i(TAG, "ACL Account: " + mApplication.getAclAdminAccount());
        Log.i(TAG, "ACL User: " + mApplication.getAclAdminUser());
        if (mApplication.getAclAdminAccount() > 1) {
            AdmAccount admAccount = AdmAccount.newInstance();
            frags.add(admAccount);
        }
        AdmUser admUser = AdmUser.newInstance();
        AdmGroup admGroup = AdmGroup.newInstance();
        AdmDevice admDevice = AdmDevice.newInstance();
        frags.add(admUser);
        frags.add(admGroup);
        frags.add(admDevice);
        return frags;
    }
}

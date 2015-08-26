package com.umaps.gpshandleclient.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.Session;
import com.umaps.gpshandleclient.event.UpdateEvent;
import com.umaps.gpshandleclient.util.PagerAdapter;
import com.umaps.gpshandleclient.view.GenericViewFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class AdmDispatcher extends GenericViewFragment {
    private static final String TAG = "AdminDispatcher";
    private MyApplication mApplication;
    private View view;
    private ViewPager mViewPager;

    public static AdmDispatcher newInstance(){
        return new AdmDispatcher();
    }

    public AdmDispatcher(){
        super();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_pager_admin, container, false);
        Session.setSessionId(2);
        EventBus.getDefault().post(new UpdateEvent.OnLive(false));
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager_admin);
        mApplication = MyApplication.getInstance();

        List<Fragment> fragments = setupFragments();
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
        return view;
    }
    private List<Fragment> setupFragments() {
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

        List<Fragment> frags = new ArrayList<>();

        AdmAccount admAccount = AdmAccount.newInstance();
        AdmUser admUser = AdmUser.newInstance();
        AdmGroup admGroup = AdmGroup.newInstance();
        AdmDevice admDevice = AdmDevice.newInstance();

        frags.add(admAccount);
        frags.add(admUser);
        frags.add(admGroup);
        frags.add(admDevice);
        return frags;
    }
}

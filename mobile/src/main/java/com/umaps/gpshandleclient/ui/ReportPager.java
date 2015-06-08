package com.umaps.gpshandleclient.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.reports.MultiChartsReporting;
import com.umaps.gpshandleclient.util.ReportPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beou on 04/06/2015.
 */
public class ReportPager extends Fragment {
    public static ReportPager newInstance() {
        return new ReportPager();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_pager_report, container, false);
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.view_pager_report);
        List<Fragment> fragments = getFragments();
        ReportPagerAdapter mPageAdapter = new ReportPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mPageAdapter);
        return view;
    }
    private List<Fragment> getFragments() {
        List<Fragment> frags = new ArrayList<>();
        OverviewChart overviewChart = OverviewChart.newInstance();
        EventCountChart eventCountChart = EventCountChart.newInstance();
        //MultiChartsReporting multiChartsReporting = MultiChartsReporting.newInstance();
        frags.add(overviewChart);
        frags.add(eventCountChart);
        //frags.add(multiChartsReporting);
        return frags;
    }
}

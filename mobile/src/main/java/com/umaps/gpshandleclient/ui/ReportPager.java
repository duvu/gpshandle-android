package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.event.UpdateEvent;
import com.umaps.gpshandleclient.util.EBus;
import com.umaps.gpshandleclient.util.PagerAdapter;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpshandleclient.view.GenericViewFragment;
import com.umaps.gpssdk.GpsRequest;
import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by beou on 04/06/2015.
 */
public class ReportPager extends GenericViewFragment {

    private static final String TAG = "ReportPager";
    private static final String TAG_REQUEST = "ReportPager";

    private View view;
    private ViewPager mViewPager;
    private View mBarProgress;
    private View mProgress;

    private GpsRequest mRequestGetGroup;
    ArrayList<Group> groupsList;
    MyApplication mApplication;
    private Typeface mTf;
    public static ReportPager newInstance() {
        return new ReportPager();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_pager_report, container, false);
        GpsSdk.setSessionId(1);
        mViewPager = (ViewPager)view.findViewById(R.id.view_pager_report);

        EventBus.getDefault().post(new UpdateEvent.OnLive(false));

        mApplication = MyApplication.getInstance();
        mTf = mApplication.getIconFont();
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);

        updateAllFragment();
        return view;
    }
    private List<Fragment> getFragments() {
        List<Fragment> frags = new ArrayList<>();
        RptOverview rptOverview = RptOverview.newInstance();
        RptEventCount rptEventCount = RptEventCount.newInstance();
        frags.add(rptOverview);
        frags.add(rptEventCount);
        return frags;
    }

    public void updateAllFragment() {
        List<Fragment> fragments = getFragments();
        PagerAdapter mPageAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            View rO = view.findViewById(R.id.selected_rpt_overview);
            View rE = view.findViewById(R.id.selected_rpt_event_count);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rO.setBackgroundColor(getResources().getColor(R.color.selected));
                        rE.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        break;
                    case 1:
                        rO.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        rE.setBackgroundColor(getResources().getColor(R.color.selected));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        View lRptOverview = view.findViewById(R.id.l_rpt_overview);
        View lRptEventCount = view.findViewById(R.id.l_rpt_event_count);
        lRptOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, true);
            }
        });
        lRptEventCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, true);
            }
        });
    }

    @EBus
    public void onEventMainThread(UpdateEvent.GroupChanged groupChanged) {
        updateAllFragment();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
            mViewPager.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mBarProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private JSONObject createParams(){
        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<>();
        fields.add("accountID");
        fields.add("groupID");
        fields.add("description");
        fields.add("pushpinID");
        fields.add("displayName");
        fields.add("deviceCount");
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParamsObject;
    }

}

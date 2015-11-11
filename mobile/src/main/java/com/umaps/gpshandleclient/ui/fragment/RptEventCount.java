package com.umaps.gpshandleclient.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.Utils;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.util.GPSColors;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpssdk.GpsRequest;
import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.MyResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by beou on 06/06/2015.
 */
public class RptEventCount extends Fragment {
    private static final String TAG = "EventCountChart";
    private HorizontalBarChart mChart;
    private View mBarProgress;
    private View mProgress;
    private GpsRequest mRequest;

    private static final String TAG_REQUEST = "rptEventCount";
    public static RptEventCount newInstance(){
        return new RptEventCount();
    }
    public RptEventCount(){
        super();
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_event_count_report, container, false);
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        MyApplication mApplication = MyApplication.getInstance();
        Utils.init(getResources());
        mChart = (HorizontalBarChart) view.findViewById(R.id.event_count_chart);
        mRequest = new GpsRequest();
        mRequest.setAccountID(GpsSdk.getAccountId());
        mRequest.setUserID(GpsSdk.getUserId());
        mRequest.setPassword(GpsSdk.getUserPassword());
        mRequest.setMethod(Request.Method.GET);
        String url = String.format(GpsRequest.CHART_SUMMARY_URL,
                GpsSdk.getSessionToken(), GpsSdk.getSelectedGroup());
        mRequest.setUrl(url);

        mRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                showProgress(false);
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;

                JSONArray mData = (JSONArray) mRes.getData();
                ArrayList<BarEntry> eventYVal = new ArrayList<>();
                ArrayList<BarEntry> odoYVal = new ArrayList<>();
                ArrayList<String> xHBVals = new ArrayList<>();
                for (int i = 0; i < mData.length(); i++) {
                    try {
                        ECModel ec = new ECModel(mData.getString(i));
                        Log.d(TAG, "" + ec.getCount());
                        Log.d(TAG, "" + ec.getDistance());

                        eventYVal.add(new BarEntry((float) ec.getCount(), i));
                        odoYVal.add(new BarEntry(ec.getDistance(), i));

                        String desc = ec.getDescription();
                        desc = desc.length() > 16 ? desc.substring(0, 15) : desc;
                        xHBVals.add(desc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //-- color
                ArrayList<Integer> colors = new ArrayList<>();
                for (int c : GPSColors.RUNNING_COLORS) {
                    colors.add(c);
                }
                for (int c : GPSColors.IDLING_COLORS) {
                    colors.add(c);
                }
                for (int c : GPSColors.STOPPED_COLORS) {
                    colors.add(c);
                }

                //-- Horizontal-BarChart
                BarDataSet eventDataSet = new BarDataSet(eventYVal, getString(R.string.bar_events));//"Events");
                BarDataSet odoDataSet = new BarDataSet(odoYVal, getString(R.string.bar_odo_meter));//"Odo-meter");

                eventDataSet.setColor(colors.get(0));
                odoDataSet.setColor(colors.get(1));

                //eventDataSet.setBarSpacePercent(1f);
                ArrayList<BarDataSet> barDataSets = new ArrayList<>();
                barDataSets.add(eventDataSet);
                barDataSets.add(odoDataSet);

                BarData barData = new BarData(xHBVals, barDataSets);
                mChart.setData(barData);

                // apply styling
                mChart.setDescription(getString(R.string.rpt_event_count_desc));
                //mChart.setMinimumHeight(200);
                //mChart.setMinimumWidth(200);
                mChart.setDrawGridBackground(true);
                mChart.setDrawBarShadow(true);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int layoutH, layoutW;
                layoutH = 80 + mData.length() * 120;
                //layoutH = displayMetrics.heightPixels;
                //layoutH = (layoutH >= (mData.length() * 100) ? layoutH : mData.length() * 100);
                layoutW = displayMetrics.widthPixels;
                mChart.setLayoutParams(new LinearLayout.LayoutParams(layoutW, layoutH));
                mChart.invalidate();

            }
        });
        mRequest.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        mRequest.exec();
        showProgress(true);
        return view;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        GpsRequest.getInstance().cancelAll();
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

            mChart.setVisibility(show ? View.GONE : View.VISIBLE);
            mChart.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mChart.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mChart.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private class ECModel {
        private String id;
        private String description;
        private int count;
        private float distance;

        public ECModel(String data){
            Log.d(TAG, data);
            if (StringTools.isBlank(data)) return;
            String[] mDat = data.split("\\|", -1);
            if (mDat.length < 4) return;
            this.id = mDat[0];
            this.description = mDat[1];
            this.count = Integer.parseInt(mDat[2]);
            this.distance = Float.parseFloat(mDat[3]);
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

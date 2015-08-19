package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.Session;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.util.GPSColors;
import com.umaps.gpshandleclient.util.GpsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by beou on 04/06/2015.
 */
public class RptOverview extends Fragment {
    private static final String TAG = "OverviewChart";
    private PieChart mChart;
    private View mBarProgress;
    private View mProgress;

    private GpsRequest mRequest;
    private static final String TAG_REQUEST = "tptOverview";

    private View view;

    MyApplication mApplication;

    private Typeface mTf;

    public static RptOverview newInstance() {
        return new RptOverview();
    }
    public RptOverview(){
        super();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "create overview report");
        view = inflater.inflate(R.layout.frag_overview_report, container, false);
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mApplication = MyApplication.getInstance();
        mTf = mApplication.getIconFont();

        //mCallback = (ReportPager) getParentFragment();

        invalidated();
        return view;
    }

    private void invalidated(){
        Utils.init(getResources());
        mChart = (PieChart) view.findViewById(R.id.event_count_chart);
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(50f);
        mChart.setRotationEnabled(false);


        mRequest = new GpsRequest(getActivity());

        mRequest.setAccountID(Session.getAccountId());
        mRequest.setUserID(Session.getUserId());
        mRequest.setPassword(Session.getUserPassword());
        mRequest.setMethod(Request.Method.GET);

        String url = String.format(GpsRequest.CHART_STATE_URL, Session.getSessionToken(), mApplication.getSelGroup());
        mRequest.setUrl(url);
        mRequest.setRequestTag(TAG_REQUEST);

        //-- prepare piechart data
        PieData pieData = new PieData();

        mRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                showProgress(false);
                MyResponse mRes = new MyResponse(response);
                int intRunning = 0;
                int intIdling = 0;
                int intStopped = 0;
                if (mRes.isExpire()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                };
                JSONArray mData = (JSONArray)mRes.getData();
                for (int i = 0; i < mData.length(); i++){
                    String stringData = null;
                    try {
                        stringData = mData.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String[] arrayData = stringData.split("\\|", -1);
                    int state = Integer.parseInt(arrayData[2]);
                    if (state == 0){
                        intStopped++;
                    } else if (state == 1){
                        intIdling++;
                    } else {
                        intRunning++;
                    }
                }

                int totalDevice = mData.length();
                String[] mParties = new String[]{
                        getString(R.string.status_running),
                        getString(R.string.status_idling),
                        getString(R.string.status_stopped)
                };
                //-- init xVals
                ArrayList<String> xVals = new ArrayList<>();
                for (int i = 0; i <mParties.length; i++){
                    xVals.add(mParties[i]);
                }
                //-- init dataSet
                ArrayList<Entry> yVals = new ArrayList<>();

                yVals.add(new Entry((float)intRunning/*/totalDevice*100*/, 1));
                yVals.add(new Entry((float)intIdling/*/totalDevice*100*/, 2));
                yVals.add(new Entry((float)intStopped/*/totalDevice*100*/, 3));
                PieDataSet dataSet = new PieDataSet(yVals, null/*label*/);
                dataSet.setSliceSpace(3f);

                //-- color
                ArrayList<Integer> colors = new ArrayList<>();
                for (int c : GPSColors.RUNNING_COLORS){
                    colors.add(c);
                }
                for (int c : GPSColors.IDLING_COLORS){
                    colors.add(c);
                }
                for (int c : GPSColors.STOPPED_COLORS){
                    colors.add(c);
                }
                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                PieData data = new PieData(xVals, dataSet);
                //data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);
                mChart.setUsePercentValues(false);
                mChart.setCenterTextSize(32);
                mChart.setCenterText(""+totalDevice);
                Legend l = mChart.getLegend();
                l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
                mChart.invalidate();

            }
        });
        mRequest.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
            }
        });
        mRequest.exec();
        showProgress(true);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        //mChart = null;
        //mBarProgress = null;
        //mProgress = null;
        mRequest.cancel(TAG_REQUEST);
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


    /*public interface RptOverviewCallback{
        public void updateAllFragment();
    }*/
}

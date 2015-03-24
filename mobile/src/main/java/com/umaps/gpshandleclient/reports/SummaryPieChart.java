package com.umaps.gpshandleclient.reports;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.util.HTTPRequestQueue;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class SummaryPieChart extends Fragment {
    private static final String TAG = "SummaryPieChart";
    private static PieChart pieChart = null;
    public static SummaryPieChart newInstance(){
        return new SummaryPieChart();
    }
    public SummaryPieChart(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        try {
            getSummaryData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        if (pieChart == null) {
            pieChart = new PieChart(getActivity());
//        }
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.summary_report);
        pieChart.setData(null);

        //--
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.highlightValues(null);
//        ((LinearLayout)view).removeView(pieChart);
        relativeLayout.addView(pieChart);
        return view;
    }
    private void generatePieData(JSONObject inputData){
        if (inputData==null){
            return;
        }
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
        //-- inputData != null now
        int intRunning = 0;
        int intIdling = 0;
        int intStopped = 0;
        double totalKM = 0.0D;
        long totalEvent = 0L;
        long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
        try {
            JSONObject jsonReport = inputData.getJSONObject(StringTools.KEY_REPORT);
            if (jsonReport == null){
                return;
            }
            JSONArray jsonReportBody = jsonReport.getJSONArray(StringTools.KEY_REPORT_BODY);
            if ((jsonReportBody==null) || (jsonReportBody.length() == 0)){
                return;
            }
            for (int i = 0; i< jsonReportBody.length(); i++){
                String itemR = jsonReportBody.getString(i);
                if (StringTools.isBlank(itemR)) continue;
                String[] childItems = itemR.split("\\|");
                long timestamp = Long.parseLong(childItems[2]);
                double odoMetter = Double.parseDouble(childItems[3]);
                totalKM+=odoMetter;
                if ((!StringTools.isBlank(childItems[5])) && (!StringTools.isBlank(childItems[5].replace("%", "")))){
                    double batteryLevel = Double.parseDouble(childItems[5].replace("%",""));
                }
                long eventCount = Long.parseLong(childItems[6]);
                totalEvent+=eventCount;
                long timeAge = currentTime - timestamp;
                if (timeAge <= 300){
                    intRunning++;
                } else if ((timeAge>300) && (timeAge <= 1800)){
                    intIdling++;
                } else if (timeAge>1800){
                    intStopped++;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int totalDevice = intRunning+intIdling+intStopped;
        yVals.add(new Entry((float)intRunning/totalDevice*100, 1));
        yVals.add(new Entry((float)intIdling/totalDevice*100, 2));
        yVals.add(new Entry((float)intStopped/totalDevice*100, 3));

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
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        StringBuffer sb = new StringBuffer();
        sb.append(SessionState.getSelGroupDesc()).append(": ").append(totalDevice).append("\n");
        sb.append("Total Km: ").append(totalKM).append("\n");
        sb.append("Total Events: ").append(totalEvent);
        pieChart.setCenterText(sb.toString());
        pieChart.setData(data);
//        Legend l = pieChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        pieChart.invalidate();
//        return data;
    }

    public void getSummaryData() throws JSONException {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put(StringTools.FLD_groupID, SessionState.getSelGroup());
        JSONObject jsonRequest = StringTools.createRequest(StringTools.CMD_GET_CHART_SUMMARY,
                Locale.getDefault().getLanguage(),jsonParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getChartUrl(),
                jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i(TAG, response.toString());
                generatePieData(response);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
            }
        });
        HTTPRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}


package com.umaps.gpshandleclient.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.reports.chart.ChartItem;
import com.umaps.gpshandleclient.reports.chart.HorizontalBarChartItem;
import com.umaps.gpshandleclient.reports.chart.PieChartItem;
import com.umaps.gpshandleclient.reports.model.ItemSummary;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.util.GpsOldRequest;
import com.umaps.gpshandleclient.util.HttpQueue;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by beou on 14/03/2015.
 */
public class MultiChartsReporting extends Fragment {
    private static ArrayList<ChartItem> listChart = null;
    ProgressDialog pd;
    private static ListView listView = null;
    private static final String TAG = "MultiChartsReporting";

    private MyApplication mApplication;

    public static MultiChartsReporting newInstance(){
        return new MultiChartsReporting();
    }
    public MultiChartsReporting(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_report, container, false);
        mApplication = MyApplication.getInstance();

        Utils.init(getResources());
        listView = (ListView) view.findViewById(R.id.chartListView);
        getScreenSize();
        try {
            getSummaryData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {
        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return getItem(position).getView(position, convertView, getContext());
        }
        @Override
        public int getItemViewType(int position){
            //-- return the views type
            return getItem(position).getItemType();
        }
        @Override
        public int getViewTypeCount(){
            return 3; //-- we have 3 different item-types
        }
    }
    //----------------------------------------------------------------------------------------------
    private void generatePieData(JSONObject inputData){
        listChart = new ArrayList<>();

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
        ArrayList<ItemSummary> summaryArrayList = new ArrayList<>();
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
                ItemSummary itemSummary = new ItemSummary();

                String itemR = jsonReportBody.getString(i);
                if (StringTools.isBlank(itemR)) continue;
                String[] childItems = itemR.split("\\|");
                String description = childItems[1];
                long timestamp = Long.parseLong(childItems[2]);
                double distance = Double.parseDouble(childItems[3]);
                String geoPoint = childItems[4];
                if (!StringTools.isBlank(geoPoint)) {
                    double lat = Double.parseDouble(geoPoint.split("\\/")[0]);
                    double lon = Double.parseDouble(geoPoint.split("\\/")[1]);

                    itemSummary.setLatitude(lat);
                    itemSummary.setLongitude(lon);
                }
                totalKM+=distance;
                double batteryLevel = 0.0D;
                String trimNumber = (!StringTools.isBlank(childItems[5])?childItems[5].replaceAll("[^0-9.]", ""):"");
                if (!StringTools.isBlank(trimNumber)){
                    batteryLevel = Double.parseDouble(trimNumber);
                }
                long eventCount = Long.parseLong(childItems[6]);

                itemSummary.setDescription(description);
                itemSummary.setTimestamp(timestamp);
                itemSummary.setDistance(distance);
                itemSummary.setBatteryLevel(batteryLevel);
                itemSummary.setEventCount(eventCount);

                summaryArrayList.add(itemSummary);
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
        //data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
//        StringBuffer sb = new StringBuffer();
//        sb.append(SessionState.getSelGroupDesc()).append(": ").append(totalDevice).append("\n");
//        sb.append(getText(R.string.total_km)).append(": ").append(totalKM).append("\n");
//        sb.append(getText(R.string.total_events)).append(": ").append(totalEvent);
        PieChartItem pieChartItem = new PieChartItem(data, getActivity());
        pieChartItem.setGroup(mApplication.getSelGroup());
        pieChartItem.setTotalEvent(totalEvent);
        pieChartItem.setTotalKm(totalKM);
        pieChartItem.setCenterText("" + totalDevice);
        pieChartItem.setWidth((int)(layoutW *0.7));
        listChart.add(pieChartItem);

        //-- Horizontal-BarChart
        ArrayList<BarEntry> yHBVal = new ArrayList<>();
        ArrayList<BarEntry> odoYVal = new ArrayList<>();
        for (int i = 0; i< totalDevice; i++){
            yHBVal.add(new BarEntry((float)summaryArrayList.get(i).getEventCount(), i));
            odoYVal.add(new BarEntry((float)summaryArrayList.get(i).getDistance(), i));
        }

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
        BarDataSet barDataSet = new BarDataSet(yHBVal, getString(R.string.bar_events));//"Events");
        BarDataSet odoDataSet = new BarDataSet(odoYVal, getString(R.string.bar_odo_meter));//"Odo-meter");
        barDataSet.setColor(colors.get(0));
        barDataSet.setBarSpacePercent(1f);
        odoDataSet.setColor(colors.get(1));
        barDataSets.add(barDataSet);
        barDataSets.add(odoDataSet);

        ArrayList<String> xHBVals = new ArrayList<>();
        for (int i = 0; i< totalDevice; i++){
            String desc = summaryArrayList.get(i).getDescription();
            desc = desc.length() > 16 ? desc.substring(0, 15) : desc;
            xHBVals.add(desc);
        }
        BarData barData = new BarData(xHBVals, barDataSets);
        HorizontalBarChartItem hbChartItem = new HorizontalBarChartItem(barData, getActivity());
        hbChartItem.setWidth(layoutW);
        hbChartItem.setHeight(totalDevice*80);
        listChart.add(hbChartItem);

        //-- add charts
        ChartDataAdapter cda = new ChartDataAdapter(getActivity(), listChart);
        listView.setAdapter(cda);
    }

    public void getSummaryData() throws JSONException {
        //pd = Utilities.ShowProgress(getActivity(), "", getString(R.string.application_loading));
        JSONObject jsonParam = new JSONObject();
        jsonParam.put(StringTools.FLD_groupID, mApplication.getSelGroup());
        JSONObject jsonRequest = StringTools.createRequest(
                mApplication.getAccountID(), mApplication.getUserID(), mApplication.getPassword(),
                StringTools.CMD_GET_CHART_SUMMARY,
                Locale.getDefault().getLanguage(),jsonParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                GpsOldRequest.CHART_URL,
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
        HttpQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private int layoutH, layoutW;
    private void getScreenSize(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.layoutH = displayMetrics.heightPixels;
        this.layoutW = displayMetrics.widthPixels;
    }
}

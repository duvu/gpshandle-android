package com.umaps.gpshandleclient.reports;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.utils.Utils;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.reports.chart.BarChartItem;
import com.umaps.gpshandleclient.reports.chart.ChartItem;
import com.umaps.gpshandleclient.reports.chart.HorizontalBarChartItem;
import com.umaps.gpshandleclient.reports.chart.LineChartItem;
import com.umaps.gpshandleclient.reports.chart.PieChartItem;
import com.umaps.gpshandleclient.reports.model.ItemSummary;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by beou on 14/03/2015.
 */
public class MultiChartsReporting extends Fragment {
    private static ArrayList<ChartItem> listChart = null;
    private static ListView listView = null;
    private static final String TAG = "MultiChartsReporting";
    public static MultiChartsReporting newInstance(){
        return new MultiChartsReporting();
    }
    public MultiChartsReporting(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_report, container, false);
        Utils.init(getResources());
        listView = (ListView) view.findViewById(R.id.chartListView);

//        if (listChart == null) {
//            listChart = new ArrayList<>();
//        }
        //--TODO: generate list of chart here and add to #listChart
        //--1. pieChart for summary
        try {
//            listChart = new ArrayList<>();
//            Utilities.ShowProgress(getActivity(), "", getString(R.string.application_loading));
            getSummaryData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        ChartData pieChartData = generateDataPie();
//        listChart.add(new PieChartItem(pieChartData, getActivity()));
//        // 30 items
//        for (int i = 0; i < 30; i++) {
//
//            if(i % 3 == 0) {
//                listChart.add(new LineChartItem(generateDataLine(i + 1), getActivity()));
//            } else if(i % 3 == 1) {
//                listChart.add(new BarChartItem(generateDataBar(i + 1), getActivity()));
//            } else if(i % 3 == 2) {
//                listChart.add(new PieChartItem(generateDataPie(i + 1), getActivity()));
//            }
//        }


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

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e1.add(new Entry((int) (Math.random() * 65) + 40, i));
        }

        LineDataSet d1 = new LineDataSet(e1, "New DataSet " + cnt + ", (1)");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e2.add(new Entry(e1.get(i).getVal() - 30, i));
        }

        LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleSize(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(getMonths(), sets);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry((int) (Math.random() * 70) + 30, i));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(getMonths(), d);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private PieData generateDataPie(/*int cnt*/) {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new Entry((int) (Math.random() * 70) + 30, i));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(getQuarters(), d);
        return cd;
    }

    private ArrayList<String> getQuarters() {

        ArrayList<String> q = new ArrayList<String>();
        q.add("1st Quarter");
        q.add("2nd Quarter");
        q.add("3rd Quarter");
        q.add("4th Quarter");

        return q;
    }

    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
    }
    //----------------------------------------------------------------------------------------------
    private void generatePieData(JSONObject inputData){
        listChart = new ArrayList<>();

        if (inputData==null){
            return;
        }

        ArrayList<ItemSummary> summaryArrayList = new ArrayList<>();

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
        StringBuffer sb = new StringBuffer();
        sb.append(SessionState.getSelGroupDesc()).append(": ").append(totalDevice).append("\n");
        sb.append("Total Km: ").append(totalKM).append("\n");
        sb.append("Total Events: ").append(totalEvent);
        PieChartItem pieChartItem = new PieChartItem(data, getActivity());
        pieChartItem.setCenterText(sb.toString());
        listChart.add(pieChartItem);
        //-- Horizontal-BarChart
        ArrayList<BarEntry> yHBVal = new ArrayList<>();
        ArrayList<BarEntry> odoYVal = new ArrayList<>();
        for (int i = 0; i< totalDevice; i++){
            yHBVal.add(new BarEntry((float)summaryArrayList.get(i).getEventCount()+1, i));
            odoYVal.add(new BarEntry((float)summaryArrayList.get(i).getDistance()+1, i));
        }

        BarDataSet barDataSet = new BarDataSet(yHBVal, getString(R.string.bar_events));//"Events");
        BarDataSet odoDataSet = new BarDataSet(odoYVal, getString(R.string.bar_odo_meter));//"Odo-meter");

        barDataSet.setColor(colors.get(0));
        odoDataSet.setColor(colors.get(1));

        barDataSet.setBarSpacePercent(1f);

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);
        barDataSets.add(odoDataSet);

        ArrayList<String> xHBVals = new ArrayList<>();
        for (int i = 0; i< totalDevice; i++){
            xHBVals.add(summaryArrayList.get(i).getDescription());
        }
        BarData barData = new BarData(xHBVals, barDataSets);
        HorizontalBarChartItem hbChartItem = new HorizontalBarChartItem(barData, getActivity());
        listChart.add(hbChartItem);

        //-- add charts
        ChartDataAdapter cda = new ChartDataAdapter(getActivity(), listChart);
        listView.setAdapter(cda);

        Utilities.HideProgress();
//        pieChart.setCenterText(sb.toString());
//        pieChart.setData(data);
//        Legend l = pieChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
//        pieChart.invalidate();
//        return data;
    }

    public void getSummaryData() throws JSONException {
        Utilities.ShowProgress(getActivity(), "", getString(R.string.application_loading));
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

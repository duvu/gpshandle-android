package com.umaps.gpshandleclient.reports.chart;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.umaps.gpshandleclient.R;

public class HorizontalBarChartItem extends ChartItem {

    private Typeface mTf;
    int w, h;

    public void setWidth(int width){
        w = width;
    }
    public int getWidth(){
        return w;
    }

    public void setHeight(int height){
        h = height;
    }
    public int getHeight(){
        return h;
    }

    public HorizontalBarChartItem(ChartData<?> cd, Context c) {
        super(cd);

        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_HBARCHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.list_item_horizontal_barchart, null);
            holder.chart = (HorizontalBarChart) convertView.findViewById(R.id.event_count_chart);
            holder.chart.setLayoutParams(new LinearLayout.LayoutParams(getWidth(), getHeight()));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // apply styling
        holder.chart.setDescription("");
        holder.chart.setMinimumHeight(200);
        holder.chart.setMinimumWidth(200);
        holder.chart.setDrawGridBackground(true);
        holder.chart.setDrawBarShadow(true);




        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5);
        leftAxis.setSpaceTop(20f);


        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5);
        rightAxis.setSpaceTop(20f);

        mChartData.setValueTypeface(mTf);

        // set data
        holder.chart.setData((BarData) mChartData);

        // do not forget to refresh the chart
//        holder.chart.invalidate();
        holder.chart.animateY(900);

        return convertView;
    }

    private static class ViewHolder {
        HorizontalBarChart chart;
    }
}
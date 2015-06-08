
package com.umaps.gpshandleclient.reports.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.umaps.gpshandleclient.R;

public class PieChartItem extends ChartItem {
    private String centerText;

    private String groupDesc;
    private double  totalKm;
    private long    totalEvent;

    private int w;
    public void setWidth(int width){
        this.w = width;
    }

    private Typeface mTf;
    public PieChartItem(ChartData<?> cd, Context c) {
        super(cd);
        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    public String getCenterText() {
        return centerText;
    }
    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    public void setGroup(String groupDesc){
        this.groupDesc = groupDesc;
    }
    public String getGroup(){
        return this.groupDesc;
    }

    public void setTotalKm(double km){
        this.totalKm = km;
    }
    public double getTotalKm(){
        return this.totalKm;
    }

    public void setTotalEvent(long event){
        this.totalEvent = event;
    }
    public long getTotalEvent(){
        return this.totalEvent;
    }

    @Override
    public int getItemType() {
        return TYPE_PIECHART;
    }
    @Override
    public View getView(int position, View convertView, Context c) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.list_item_piechart, null);

            holder.infoView = (View) convertView.findViewById(R.id.piechartInfo);
            holder.chart = (PieChart) convertView.findViewById(R.id.event_count_chart);
            holder.chart.setLayoutParams(new LinearLayout.LayoutParams(w, w));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.setDescription("");
        holder.chart.setHoleRadius(52f);
        holder.chart.setTransparentCircleRadius(57f);
        holder.chart.setCenterText(getCenterText());
        holder.chart.setCenterTextTypeface(mTf);
        holder.chart.setCenterTextSize(18f);
        holder.chart.setUsePercentValues(true);
        holder.chart.setRotationEnabled(false);

        TextView groupDesc = (TextView) holder.infoView.findViewById(R.id.groupDesc);
        groupDesc.setText(R.string.title_group);
        TextView groupDescText = (TextView) holder.infoView.findViewById(R.id.groupDescText);
        groupDescText.setText(getGroup());

        TextView totalKm = (TextView) holder.infoView.findViewById(R.id.totalKm);
        totalKm.setText(R.string.total_km);
        TextView totalKmText = (TextView) holder.infoView.findViewById(R.id.totalKmText);
        totalKmText.setText(""+getTotalKm());

        TextView totalEvent = (TextView) holder.infoView.findViewById(R.id.totalEvent);
        totalEvent.setText(R.string.total_events);
        TextView totalEventText = (TextView) holder.infoView.findViewById(R.id.totalEventText);
        totalEventText.setText(""+getTotalEvent());

        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTypeface(mTf);
        mChartData.setValueTextSize(11f);
        mChartData.setValueTextColor(Color.WHITE);

        holder.chart.setData((PieData) mChartData);

        Legend l = holder.chart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_RIGHT);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        holder.chart.animateXY(900, 900);

        return convertView;
    }

    private static class ViewHolder {
        View infoView;
        PieChart chart;
    }
}
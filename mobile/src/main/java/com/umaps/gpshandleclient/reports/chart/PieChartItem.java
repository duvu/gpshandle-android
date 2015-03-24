
package com.umaps.gpshandleclient.reports.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.umaps.gpshandleclient.R;

public class PieChartItem extends ChartItem {
    private String centerText;
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
            holder.chart = (PieChart) convertView.findViewById(R.id.chart);
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

        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTypeface(mTf);
        mChartData.setValueTextSize(11f);
        mChartData.setValueTextColor(Color.WHITE);

        holder.chart.setData((PieData) mChartData);

        Legend l = holder.chart.getLegend();
//        l.setPosition(LegendPosition.RIGHT_OF_CHART_CENTER);
//        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setPosition(LegendPosition.RIGHT_OF_CHART);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        holder.chart.animateXY(900, 900);

        return convertView;
    }

    private static class ViewHolder {
        PieChart chart;
    }
}
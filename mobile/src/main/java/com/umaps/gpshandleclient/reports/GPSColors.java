package com.umaps.gpshandleclient.reports;

import android.graphics.Color;

/**
 * Created by beou on 14/03/2015.
 */
public class GPSColors {
    public static final int[] STOPPED_COLORS = {
            Color.parseColor("#FF0A0A")
            /*Color.rgb(255, 10, 10), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)*/
    };
    public static final int[] RUNNING_COLORS = {
            Color.rgb(6, 219, 20)/*, Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)*/
    };
    public static final int[] IDLING_COLORS = {
            Color.rgb(255, 165, 0)/*, Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)*/
    };
}

package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umaps.gpshandleclient.R;

/**
 * Created by vu@umaps.vn on 29/10/2014.
 */
public class MarkerTool {

    public static Bitmap getMarker(Context context, String deviceDesc, double speed, double heading){
        Bitmap rtnBitmap = null;
        LinearLayout tv = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.marker, null, false);
        TextView labelTextView = (TextView)tv.findViewById(R.id.marker_label_id);
        labelTextView.setText(deviceDesc);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        ImageView imageView = (ImageView) tv.findViewById(R.id.marker_image);

        imageView.setImageResource(evalIcon(speed, heading));

        //-- left, top, right, bottom
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        tv.setDrawingCacheEnabled(true);
        tv.buildDrawingCache();
        rtnBitmap = tv.getDrawingCache();

        return  rtnBitmap;
    }

    public static int evalIcon(double speed, double heading){
        int _tres = 0;
        if(speed <= 5){
            _tres = evalRedIcon(heading);
        } else if (speed < 32){
            _tres = evalYellowIcon(heading);
        } else if(speed >= 32){
            _tres = evalGreenIcon(heading);
        }
        return _tres;
    }

    private static int evalRedIcon(double heading){
        return R.drawable.car_red;
    }
    private static int evalYellowIcon(double heading){
        int _head = ((int)(heading)/45) % 8;
        switch (_head){
            case 0:
                return R.drawable.car_yellow_h0;
            case 1:
                return R.drawable.car_yellow_h1;
            case 2:
                return R.drawable.car_yellow_h2;
            case 3:
                return R.drawable.car_yellow_h3;
            case 4:
                return R.drawable.car_yellow_h4;
            case 5:
                return R.drawable.car_yellow_h5;
            case 6:
                return R.drawable.car_yellow_h6;
            case 7:
                return R.drawable.car_yellow_h7;
        }
        return R.drawable.car_yellow_h0;
    }

    private static int evalGreenIcon(double heading){
        int _head = ((int)(heading)/45) % 8;
        switch (_head){
            case 0:
                return R.drawable.car_green_h0;
            case 1:
                return R.drawable.car_green_h1;
            case 2:
                return R.drawable.car_green_h2;
            case 3:
                return R.drawable.car_green_h3;
            case 4:
                return R.drawable.car_green_h4;
            case 5:
                return R.drawable.car_green_h5;
            case 6:
                return R.drawable.car_green_h6;
            case 7:
                return R.drawable.car_green_h7;
        }
        return R.drawable.car_green_h0;
    }
}
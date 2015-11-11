package com.umaps.gpshandleclient.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by vu@umaps.vn on 10/02/2015.
 */
public class CustomMapLayout extends RelativeLayout {
    private static final String TAG = "CustomMapLayout";
    private GoogleMap googleMap;
    private int bottomOffsetPixels;
    private Marker marker;
    private View infoWindow;

    public CustomMapLayout(Context context) {
        super(context);
    }
    public CustomMapLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public CustomMapLayout(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    //--Must call before can rout the touch events
    public void init(GoogleMap mMap, int bottomOffset){
        this.googleMap = mMap;
        this.bottomOffsetPixels = bottomOffset;
    }
    //-- best to be called from either the InfoWindowAdapter.getInfoContents or
    //-- InfoWindowAdapter.getInfoWindow
    public void setMarkerWithWindowInfo(Marker marker, View infoWindow){
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        boolean ret = false;
        //-- Make sure the infoWindow is showing and have all the needed references
        if (marker != null && marker.isInfoWindowShown() && googleMap != null && infoWindow!=null){
            Point point = googleMap.getProjection().toScreenLocation(marker.getPosition());
            //Make a copy of MotionEvent and adjust its location
            //so it is relative to the infoWindow left-top corner
            MotionEvent copyEvent = MotionEvent.obtain(event);
            copyEvent.offsetLocation(
                    -point.x + (infoWindow.getWidth()/2),
                    -point.y + (infoWindow.getHeight() + bottomOffsetPixels)
            );
            ret = infoWindow.dispatchTouchEvent(copyEvent);
        }
        return ret || super.dispatchTouchEvent(event);
    }

}

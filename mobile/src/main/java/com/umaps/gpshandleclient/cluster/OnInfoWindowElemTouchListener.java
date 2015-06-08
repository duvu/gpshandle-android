package com.umaps.gpshandleclient.cluster;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by vu@umaps.vn on 09/02/2015.
 */
public abstract class OnInfoWindowElemTouchListener implements View.OnTouchListener {
    private View view;
    private Marker marker;
    private Drawable bgNormal;
    private Drawable bgPressed;
    private boolean isPressed = false;
    final Handler handler = new Handler();

    public OnInfoWindowElemTouchListener(View v, Drawable normal, Drawable pressed){
        super();
        this.view = v;
        this.bgNormal = normal;
        this.bgPressed = pressed;
    }
    public void setMarker(Marker m){
        marker = m;
    }
    @Override
    public boolean onTouch(View vv, MotionEvent event){
        this.view = vv;
        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                0 <= event.getY() && event.getY() <= view.getHeight()){
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    //Start process
                    startPress();
                    break;
                case MotionEvent.ACTION_UP:
                    //
                    Log.i("Click On Button", "Click on a button on info-window");
                    handler.postDelayed(confirmClickRunnable, 150);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    //
                    endPress();
                    break;
                default:
                    break;

            }
        } else {
            endPress();
        }
        return false;
    }
    private final Runnable confirmClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (endPress()){
                onClickConfirm(view, marker);
            }
        }
    };
//    @SuppressLint("NewApi")
    private void setBackground(Drawable bg){
//        int currentVersion = Build.VERSION.;
//        if (currentVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ){
            view.setBackgroundDrawable(bg);
//        } else {
//            view.setBackground(bg);
//        }
    }
    private void startPress(){
        if (!isPressed){
            isPressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            setBackground(bgPressed);
            if (marker!=null){
                marker.showInfoWindow();
            }
        }
    }
    private boolean endPress(){
        if (isPressed) {
            this.isPressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            setBackground(bgNormal);
            if (marker != null){
                marker.showInfoWindow();
            }
            return true;
        } else {
            return false;
        }
    }
    protected abstract void onClickConfirm(View view, Marker marker);
}

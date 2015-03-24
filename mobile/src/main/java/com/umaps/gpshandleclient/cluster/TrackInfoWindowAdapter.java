package com.umaps.gpshandleclient.cluster;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.activities.MainActivity;
import com.umaps.gpshandleclient.model.MapData;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpshandleclient.views.CustomMapLayout;
import com.umaps.gpshandleclient.views.OnInfoWindowElemTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by beou on 29/10/2014.
 */
public class TrackInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "TrackInfoWindowAdapter";
    TrackInfoWindowCallback mCallback;

    Context context;
    LayoutInflater inflater = null;
//    IconGenerator iconFactory = null;
    CustomMapLayout mapLayout = null;
    Marker marker;
    Typeface icoMoon = null;

    public TrackInfoWindowAdapter(Context _context, CustomMapLayout customMapLayout){
        context = _context;
        mCallback = (MainActivity)_context;
        mapLayout = customMapLayout;
        inflater = (LayoutInflater.from(_context));
//        iconFactory = new IconGenerator(_context);
        icoMoon = Typeface.createFromAsset(_context.getAssets(), "icomoon.ttf");
    }
    public TrackInfoWindowAdapter(Context _context){
        context = _context;
        try {
            mCallback = (MainActivity)_context;
        } catch (ClassCastException e){
            Log.e(TAG, "Cannot cast to MainActivity");
        }
        inflater = (LayoutInflater.from(_context));
//        iconFactory = new IconGenerator(_context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        this.marker = marker;
        //--check if cluster or item
        String info = marker.getSnippet();
        if (StringTools.isBlank(info)){
            return null;
        }
        //-- info should be not null now
        Log.i("INFO-WINDOW", info);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject==null){
            return null;
        }
        //-- jsonObject should not null now
        try {
            if(jsonObject.getBoolean(MapData.MD_IS_CLUSTER)){
                Log.i("INFO-WINDOW", "You've clicked on a cluster");
                return getInfoWindowForCluster(jsonObject.getJSONObject(MapData.MD_DAtA));
            }
            else {
                return getInfoWindowForItem(jsonObject.getJSONObject(MapData.MD_DAtA));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //-- getInfoWindowForCluster
    public View getInfoWindowForCluster(JSONObject jsonObject) throws JSONException {
        View view = inflater.inflate(R.layout.cluster_group_popup, null);
        int devCount = jsonObject.getInt(MapData.MD_COUNT);
        int runningCount = jsonObject.getInt(MapData.MD_RUNNING_COUNT);
        double averageSpeed = jsonObject.getDouble(MapData.MD_AVERAGE_SPEED);

        TextView devCountText = (TextView) view.findViewById(R.id.deviceCountText);
        devCountText.setText(R.string.deviceCountText);
        TextView devCountTView = (TextView) view.findViewById(R.id.deviceCountValue);
        devCountTView.setText(""+devCount);

        TextView devRunningText = (TextView) view.findViewById(R.id.deviceRunningText);
        devRunningText.setText(R.string.deviceRunningCountText);
        TextView devRunningValue = (TextView) view.findViewById(R.id.deviceRunningValue);
        devRunningValue.setText(""+runningCount);

        TextView devAverageSpeedText = (TextView) view.findViewById(R.id.speedAverageText);
        devAverageSpeedText.setText(R.string.averageSpeedText);
        TextView devAverageSpeedValue = (TextView) view.findViewById(R.id.speedAverageValue);
        devAverageSpeedValue.setText(""+averageSpeed);
        //TODO: do something here for showing window-popup of a cluster
        return view;
    }
    //-- getInfoWindowForItem
    public View getInfoWindowForItem(JSONObject jsonObject) throws JSONException {
        View view = inflater.inflate(R.layout.cluster_item_popup, null);
        double latitude     = jsonObject.getDouble(MapData.Point.MD_LATITUDE);
        double longitude    = jsonObject.getDouble(MapData.Point.MD_LONGITUDE);

        final String id     = jsonObject.getString(MapData.Point.MD_ID);
        String vin          = jsonObject.getString(MapData.Point.MD_VIN);
        final String desc   = jsonObject.getString(MapData.Point.MD_DESC);
        long epoch          = jsonObject.getLong(MapData.Point.MD_EPOCH);
        String status       = jsonObject.getString(MapData.Point.MD_STATUS);
        String latLon       = latitude+"/"+longitude;
        double gpsAge       = jsonObject.getDouble(MapData.Point.MD_GPS_AGE);
        double creationAge  = jsonObject.getDouble(MapData.Point.MD_CREATION_AGE);
        double accuracy     = jsonObject.getDouble(MapData.Point.MD_ACCURACY);
        int satCount        = jsonObject.getInt(MapData.Point.MD_SAT_COUNT);
        double speed        = jsonObject.getDouble(MapData.Point.MD_SPEED);
        double heading      = jsonObject.getDouble(MapData.Point.MD_HEADING);
        double altitude     = jsonObject.getDouble(MapData.Point.MD_ALTITUDE);
        double odom         = jsonObject.getDouble(MapData.Point.MD_ODOM);
        boolean isStopped   = jsonObject.getBoolean(MapData.Point.MD_IS_STOPPED);
        String gpIo         = jsonObject.getString(MapData.Point.MD_GP_IO);
        String address      = jsonObject.getString(MapData.Point.MD_ADDRESS);

        double batteryLevel = jsonObject.getDouble(MapData.Point.MD_BATTERY_LEVEL);
        double signalStrength = jsonObject.getDouble(MapData.Point.MD_SIGNAL_STRENGTH);

        TextView tDesc = (TextView) view.findViewById(R.id.tDesc);
        tDesc.setText(R.string.tDesc);
        TextView cDesc = (TextView) view.findViewById(R.id.cDesc);
        cDesc.setText(desc);

        TextView icDeviceStatus     = (TextView) view.findViewById(R.id.device_status);
        TextView icDeviceSatellite  = (TextView) view.findViewById(R.id.device_satellite);
        TextView icDeviceBattery    = (TextView) view.findViewById(R.id.device_battery);
        TextView icDeviceBatteryText= (TextView) view.findViewById(R.id.device_battery_text);
        TextView icDeviceGPRS       = (TextView) view.findViewById(R.id.device_gprs_ssi);
        icDeviceStatus.setTypeface(icoMoon);
        icDeviceSatellite.setTypeface(icoMoon);
        icDeviceBattery.setTypeface(icoMoon);
        icDeviceGPRS.setTypeface(icoMoon);
        icDeviceStatus.setText(String.valueOf((char)0xf111));
        long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
        if (((currentTimestamp - epoch) > 300)&&((currentTimestamp - epoch) < 1800)){
            icDeviceStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        } else if ((currentTimestamp - epoch) > 1800){
            icDeviceStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        icDeviceSatellite.setText(String.valueOf((char)0xe600));
        if (satCount < 4){
            icDeviceSatellite.setTextColor(context.getResources().getColor(R.color.red));
        } else if (satCount <=7){
            icDeviceSatellite.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        icDeviceBattery.setText(String.valueOf((char)0xe64c));
        icDeviceBatteryText.setText(String.format("%1$,.0f", batteryLevel*100)+"%");
        if (batteryLevel < 0.2){
            icDeviceBattery.setTextColor(context.getResources().getColor(R.color.red));
            icDeviceBatteryText.setTextColor(context.getResources().getColor(R.color.red));
        } else if (batteryLevel < 0.7){
            icDeviceBattery.setTextColor(context.getResources().getColor(R.color.yellow));
            icDeviceBatteryText.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        icDeviceGPRS.setText(String.valueOf((char)0xe657));
        if (signalStrength < 0.2){
            icDeviceGPRS.setTextColor(context.getResources().getColor(R.color.red));
        } else if (signalStrength < 0.7){
            icDeviceGPRS.setTextColor(context.getResources().getColor(R.color.yellow));
        }

        TextView titlePosition = (TextView) view.findViewById(R.id.titlePosition);
        titlePosition.setText(R.string.title_position);
        TextView contentPosition = (TextView) view.findViewById(R.id.contentPosition);
        contentPosition.setText(latLon);

        TextView tSpeed = (TextView) view.findViewById(R.id.tSpeed);
        tSpeed.setText(R.string.tSpeed);
        TextView cSpeed = (TextView) view.findViewById(R.id.cSpeed);
        cSpeed.setText(speed+"km/h");

        TextView tOdom = (TextView) view.findViewById(R.id.tOdom);
        tOdom.setText(R.string.tOdom);
        TextView cOdom = (TextView) view.findViewById(R.id.cOdom);
        cOdom.setText(odom+"km");

        TextView titleAddr = (TextView) view.findViewById(R.id.titleAddr);
        titleAddr.setText(R.string.title_addr);
        final TextView contentAddr = (TextView) view.findViewById(R.id.contentAddr);
        contentAddr.setText(address);

        //--Setup listener for historical button
        //----------------------------------------------------------------------------------------//
        Button btn30min = (Button) view.findViewById(R.id.btn_historical_30m);
        btn30min.setOnTouchListener(
            new OnInfoWindowElemTouchListener(
                btn30min,
                context.getResources().getDrawable(R.drawable.common_signin_btn_icon_normal_light),
                context.getResources().getDrawable(R.drawable.common_signin_btn_icon_pressed_light)
            ) {
            @Override
            protected void onClickConfirm(View view, Marker marker) {
                Log.i(TAG, "got clicked on btn30min");
                //--load 30 min historical data
                long timeTo = Calendar.getInstance().getTimeInMillis()/1000;
                long timeFrom = timeTo - 30 * 60;

                mCallback.onTrackInfoWindowButton(id, desc, timeFrom, timeTo);
            }
        });
        //--
        Button btn60min = (Button) view.findViewById(R.id.btn_historical_60m);
        btn60min.setOnTouchListener(
                new OnInfoWindowElemTouchListener(
                        btn60min,
                        context.getResources().getDrawable(R.drawable.common_signin_btn_icon_normal_light),
                        context.getResources().getDrawable(R.drawable.common_signin_btn_icon_pressed_light)
                ) {
                    @Override
                    protected void onClickConfirm(View view, Marker marker) {
                        //--load 60 min historical data
                        Log.i(TAG, "got clicked on btn60min");
                        long timeTo = Calendar.getInstance().getTimeInMillis()/1000;
                        long timeFrom = timeTo - 60 * 60;
                        mCallback.onTrackInfoWindowButton(id, desc, timeFrom, timeTo);
                    }
                }
        );
        Button btn6h = (Button) view.findViewById(R.id.btn_historical_6h);
        btn6h.setOnTouchListener(
                new OnInfoWindowElemTouchListener(
                        btn6h,
                        context.getResources().getDrawable(R.drawable.common_signin_btn_icon_normal_light),
                        context.getResources().getDrawable(R.drawable.common_signin_btn_icon_pressed_light)
                ) {
                    @Override
                    protected void onClickConfirm(View view, Marker marker) {
                        Log.i(TAG, "got clicked on btn6h");
                        long timeTo = Calendar.getInstance().getTimeInMillis()/1000;
                        long timeFrom = timeTo - 6 * 60 * 60;
                        mCallback.onTrackInfoWindowButton(id, desc, timeFrom, timeTo);
                    }
                }
        );
        Button btn12h = (Button) view.findViewById(R.id.btn_historical_12h);
        btn12h.setOnTouchListener(
                new OnInfoWindowElemTouchListener(
                        btn12h,
                        context.getResources().getDrawable(R.drawable.common_signin_btn_icon_normal_light),
                        context.getResources().getDrawable(R.drawable.common_signin_btn_icon_pressed_light)
                ) {
                    @Override
                    protected void onClickConfirm(View view, Marker marker) {
                        Log.i(TAG, "got clicked on btn12h");
                        long timeTo = Calendar.getInstance().getTimeInMillis()/1000;
                        long timeFrom = timeTo - 12 * 60 * 60;
                        mCallback.onTrackInfoWindowButton(id, desc, timeFrom, timeTo);
                    }
                }
        );
        //----------------------------------------------------------------------------------------//
        if (mapLayout!=null){
            mapLayout.setMarkerWithWindowInfo(marker, view);
        }

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public interface TrackInfoWindowCallback{
        public void onTrackInfoWindowButton(String deviceId, String desc, long from, long to);
    }
}

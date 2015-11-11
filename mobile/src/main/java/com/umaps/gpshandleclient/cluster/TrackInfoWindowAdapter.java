package com.umaps.gpshandleclient.cluster;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.MapPoint;
import com.umaps.gpshandleclient.ui.fragment.MapFragment;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpshandleclient.view.CustomMapLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by beou on 29/10/2014.
 */
public class TrackInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, ClusterConstant {
    private static final String TAG = "TrackInfoWindowAdapter";
    TrackInfoWindowCallback mCallback;

    Context context;
    LayoutInflater inflater = null;
    CustomMapLayout mapLayout = null;
    Marker marker;
    Typeface icoMoon = null;
    MyApplication mApplication;

    public TrackInfoWindowAdapter (Fragment fragment, CustomMapLayout customMapLayout){
        this.context = fragment.getActivity();
        this.mApplication = MyApplication.getInstance();
        mCallback = (MapFragment)fragment;
        mapLayout = customMapLayout;
        inflater = LayoutInflater.from(context);
        icoMoon = mApplication.getIconFont();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        this.marker = marker;
        //--check if cluster or item
        String info = marker.getSnippet();
        if (StringTools.isBlank(info)){
            return null;
        }
        JSONObject snipet = null;
        try {
            snipet = new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(snipet==null){
            return null;
        }
        //-- snipet should not null now
        //Log.i(TAG, "##"+snipet.toString());
        try {
            if(snipet.getBoolean(IS_CLUSTER)){
                //Log.i("INFO-WINDOW", "You've clicked on a cluster");
                return getInfoWindowForCluster(snipet.getJSONObject(ITEM_DATA));
            }
            else {
                return getInfoWindowForItem(snipet.getString(ITEM_DATA));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //-- getInfoWindowForCluster
    public View getInfoWindowForCluster(JSONObject jsonObject) throws JSONException {
        View view = inflater.inflate(R.layout.cluster_group_popup, null);
        int devCount = jsonObject.getInt(CLUSTER_SIZE);
        int runningCount = jsonObject.getInt(LIVING_COUNT);
        double averageSpeed = jsonObject.getDouble(AVERAGE_SPEED);

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
    public View getInfoWindowForItem(String mapPointString) throws JSONException {
        View view = inflater.inflate(R.layout.cluster_item_popup, null);

        MapPoint mapPoint = new MapPoint(mapPointString);
        if (mapPoint == null){
            return view;
        }

        final String id     = mapPoint.getId();
        final String desc   = mapPoint.getDesc();

        TextView cDesc = (TextView) view.findViewById(R.id.txt_desc_content);
        cDesc.setText(mapPoint.getDesc());

        TextView icDeviceStatus     = (TextView) view.findViewById(R.id.ic_device_status);
        TextView icDeviceSatellite  = (TextView) view.findViewById(R.id.ic_device_satellite);
        TextView icDeviceBattery    = (TextView) view.findViewById(R.id.ic_device_battery);
        TextView icDeviceBatteryText= (TextView) view.findViewById(R.id.txt_device_battery);
        TextView icDeviceGPRS       = (TextView) view.findViewById(R.id.ic_device_gprs_ssi);
        icDeviceStatus.setTypeface(icoMoon);
        icDeviceSatellite.setTypeface(icoMoon);
        icDeviceBattery.setTypeface(icoMoon);
        icDeviceGPRS.setTypeface(icoMoon);

        icDeviceStatus.setText(String.valueOf((char)0xe6af));
        long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
        if (currentTimestamp - mapPoint.getEpoch() <= 300) {
            icDeviceStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else if (((currentTimestamp - mapPoint.getEpoch()) > 300)&&((currentTimestamp - mapPoint.getEpoch()) < 1800)){
            icDeviceStatus.setTextColor(context.getResources().getColor(R.color.warning));
        } else if ((currentTimestamp - mapPoint.getEpoch()) > 1800){
            icDeviceStatus.setTextColor(context.getResources().getColor(R.color.bad));
        }
        icDeviceSatellite.setText(String.valueOf((char)0xe6df));
        if (mapPoint.getNumSat() < 4){
            icDeviceSatellite.setTextColor(context.getResources().getColor(R.color.bad));
        } else if (mapPoint.getNumSat() <=7){
            icDeviceSatellite.setTextColor(context.getResources().getColor(R.color.warning));
        }
        icDeviceBatteryText.setText(String.format("%1$,.0f", mapPoint.getBatteryLevel()*100)+"%");
        icDeviceBattery.setText(String.valueOf((char)0xe659));
        if (mapPoint.getBatteryLevel() < 0.2){
            icDeviceBattery.setTextColor(context.getResources().getColor(R.color.bad));
            icDeviceBatteryText.setTextColor(context.getResources().getColor(R.color.bad));
        } else if (mapPoint.getBatteryLevel() < 0.6){
            icDeviceBattery.setText(String.valueOf((char)0xee656));
            icDeviceBattery.setTextColor(context.getResources().getColor(R.color.warning));
            icDeviceBatteryText.setTextColor(context.getResources().getColor(R.color.warning));
        }
        icDeviceGPRS.setText(String.valueOf((char)0xe61b));
        if (mapPoint.getSsi() < 0.2){
            icDeviceGPRS.setTextColor(context.getResources().getColor(R.color.bad));
        } else if (mapPoint.getSsi() < 0.7){
            icDeviceGPRS.setTextColor(context.getResources().getColor(R.color.warning));
        }

        TextView contentPosition = (TextView) view.findViewById(R.id.txt_position_content);
        contentPosition.setText(mapPoint.getLatitude()+"/"+mapPoint.getLongitude());
        TextView cSpeed = (TextView) view.findViewById(R.id.txt_speed_content);
        cSpeed.setText(mapPoint.getSpeedKPH()+"km/h");
        TextView cOdom = (TextView) view.findViewById(R.id.txt_odom_content);
        cOdom.setText(mapPoint.getOdoM()+"km");
        TextView contentAddr = (TextView) view.findViewById(R.id.txt_addr_content);
        contentAddr.setText(mapPoint.getAddress());

        Button txtLink = (Button) view.findViewById(R.id.txt_link_historical);
        txtLink.setText(R.string.title_historical);

        View vHistory = view.findViewById(R.id.link_historical);
        Log.d(TAG, txtLink.getWidth() + "/" + txtLink.getHeight());
        txtLink.setOnTouchListener(
            new OnInfoWindowElemTouchListener(
                vHistory,
                context.getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark),
                context.getResources().getDrawable(R.drawable.common_google_signin_btn_icon_light)
        ) {
            @Override
            protected void onClickConfirm(View view, Marker marker) {
                Log.d(TAG, "Touched!");
                mCallback.onTrackInfoWindowButton(id, desc);
            }
        });
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
        public void onTrackInfoWindowButton(String deviceId, String desc/*, long from, long to*/);
    }
}

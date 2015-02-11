package com.umaps.gpshandleclient.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;

import java.util.ArrayList;

/**
 * Created by vu@umaps.vn on 07/02/2015.
 */
public class DeviceListViewAdapter extends ArrayAdapter<Device> {
    private Context mContext;
    private ArrayList<Device> mDeviceList;

    public DeviceListViewAdapter(Context context, ArrayList<Device> deviceList){
        super(context, R.layout.gps_device, deviceList);
        mContext = context;
        mDeviceList = deviceList;
    }

    public void setDevices(ArrayList<Device> devicesList){
        mDeviceList = devicesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v==null){
            v = LayoutInflater.from(mContext).inflate(R.layout.gps_device, parent, false);
        }

        Device mDevice = getItem(position);
        if (mDevice == null) return null;
        //-- mDevice should not be null now
        String deviceID     = mDevice.getDeviceID();
        String description  = mDevice.getDescription();
        String icon         = mDevice.getIcon();
        boolean isLive      = mDevice.isLive();
        long lastEventTime  = mDevice.getLastEventTime();
        TextView deviceIDTextView = (TextView) v.findViewById(R.id.listView_deviceID);
        TextView deviceDescriptionTextView = (TextView) v.findViewById(R.id.listView_deviceDesc);
        deviceIDTextView.setText(deviceID);
        deviceDescriptionTextView.setText(description);
        return v;
    }
}

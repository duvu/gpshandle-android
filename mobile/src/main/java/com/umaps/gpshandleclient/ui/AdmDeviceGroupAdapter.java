package com.umaps.gpshandleclient.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vu@umaps.vn on 01/11/2014.
 */
public class AdmDeviceGroupAdapter extends ArrayAdapter<Group> {
    private static final String TAG = "AdmDeviceGroupAdapter";
    private Context context;
    private int mResource;
    private LayoutInflater mInflater;

    private ArrayList<Group> groupsList;
    private HashMap<String, ArrayList<Device>> devicesInGroup;
    Typeface mTf = null;

    MyApplication mApplication;

    public AdmDeviceGroupAdapter(Context context, int resource, ArrayList<Group> list) {
        super(context, resource, list);
        this.groupsList = list;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(this.mResource, parent, false);
        } else {
            view = convertView;
        }

        TextView txtGroupDesc = (TextView) view.findViewById(R.id.rpt_list_group_description);
        TextView txtGroupSize = (TextView) view.findViewById(R.id.rpt_list_group_size);

        txtGroupDesc.setText(groupsList.get(position).getDescription());
        txtGroupSize.setText(""+groupsList.get(position).getDeviceCount());

        return view;
    }
}

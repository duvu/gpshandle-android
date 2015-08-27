package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.umaps.gpshandleclient.R;
import com.umaps.gpssdk.Group;

import java.util.ArrayList;

/**
 * Created by beou on 11/06/2015.
 */
public class ReportGroupListViewAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;
    private int mResource;
    private ArrayList<Group> mData;
    public ReportGroupListViewAdapter(Context context, int resource, ArrayList<Group> list) {
        super(context, resource, list);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResource = resource;
        mData = list;
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

        txtGroupDesc.setText(mData.get(position).getDescription());
        txtGroupSize.setText(""+mData.get(position).getDeviceCount());

        return view;
    }
}

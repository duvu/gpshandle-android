package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;

import java.util.HashMap;
import java.util.List;

/**
 * Created by vu@umaps.vn on 01/11/2014.
 */
public class V9ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Group> groups; //Header group
    private HashMap<String, List<Device>> gDevices; //List of devices

    public V9ExpandableListAdapter(Context _context, List<Group>_groups, HashMap<String, List<Device>> _gDevices){
        context = _context;
        groups = _groups;
        gDevices = _gDevices;
        /*this.setContext(_context);
        this.setGroups(_groups);
        this.setGDevices(_gDevices);*/
    }

    @Override
    public int getGroupCount() {
        return this.getGDevices().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.getGDevices().get(this.getGroups().get(i).getGroupid()).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.getGroups().get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return this.getGDevices().get(this.getGroups().get(i).getGroupid()).get(i2);
    }

    @Override
    public long getGroupId(int i) {
        //this.getGroups().get(i).getGroupid();
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //View - Most-Important
    @Override
    public View getGroupView(int gprPos, boolean isExpandable, View view, ViewGroup viewGroup) {
        String gprDesc = this.getGroups().get(gprPos).getDescription();
        String grpID = this.getGroups().get(gprPos).getGroupid();

        if(view==null){
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            view = inflater.inflate(R.layout.group, null);
        }

        //final V9ExpandableListAdapter vadapter = this;
        final TextView tvViewThis = (TextView) view.findViewById(R.id.listView_viewThis);
        tvViewThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: expand/collapse
            }
        });

        TextView tvGrpID = (TextView) view.findViewById(R.id.listView_grpID);
        TextView tvGrpDesc = (TextView) view.findViewById(R.id.listView_grpDesc);
        tvGrpID.setText("["+grpID+"]");
        tvGrpDesc.setText(gprDesc);
        return view;
    }

    @Override
    public View getChildView(int gprPos, int devPos, boolean b, View view, ViewGroup viewGroup) {

        String deviceID = this.getGDevices().get(this.getGroups().get(gprPos).getGroupid()).get(devPos).getDeviceID();
        String deviceDesc = this.getGDevices().get(this.getGroups().get(gprPos).getGroupid()).get(devPos).getDescription();
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            view = inflater.inflate(R.layout.device, null);
        }
        TextView devID = (TextView) view.findViewById(R.id.listView_deviceID);
        TextView devDesc = (TextView) view.findViewById(R.id.listView_deviceDesc);
        devID.setText(deviceID);
        devDesc.setText(deviceDesc);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    //Getter/Setter
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public HashMap<String, List<Device>> getGDevices() {
        return gDevices;
    }

    public void setGDevices(HashMap<String, List<Device>> gDevices) {
        this.gDevices = gDevices;
    }
}

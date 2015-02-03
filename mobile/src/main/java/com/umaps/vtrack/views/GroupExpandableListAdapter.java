package com.umaps.vtrack.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.umaps.vtrack.R;
import com.umaps.vtrack.model.Device;
import com.umaps.vtrack.model.Group;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vu@umaps.vn on 01/11/2014.
 */
public class GroupExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Group> groups; //Header gpshandle_device_group
    private HashMap<String, ArrayList<Device>> devicesInGroup; //List of devices

    public GroupExpandableListAdapter(Context _context, ArrayList<Group> _groups, HashMap<String, ArrayList<Device>> _gDevices){
        context = _context;
        groups = _groups;
        devicesInGroup = _gDevices;
    }

    @Override
    public int getGroupCount() {
        if (groups!=null){
            return groups.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if ((groups!=null) && (devicesInGroup!=null)) {
            return devicesInGroup.get(groups.get(groupPosition).getGroupId()).size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groups != null) {
            return groups.get(groupPosition);
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if ((groups!= null) && (devicesInGroup!= null)) {
            return devicesInGroup.get(groups.get(groupPosition).getGroupId()).get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //View - Most-Important
    @Override
    public View getGroupView(int groupPosition, boolean isExpandable, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            convertView = inflater.inflate(R.layout.gpshandle_device_group, null);
        }
        String groupID          = "";
        String groupDescription = "";
        String groupIcon        = "";
        int groupSize           = 0;
        int countLive           = 0;
        if (groups!=null) {
            groupID          = groups.get(groupPosition).getGroupId();
            groupDescription = groups.get(groupPosition).getDescription();
            groupIcon        = groups.get(groupPosition).getIcon();
            groupSize        = groups.get(groupPosition).getCount();
            countLive        = groups.get(groupPosition).getLive();
        }
//        final TextView tvViewThis = (TextView) convertView.findViewById(R.id.listView_viewThis);
//        tvViewThis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //--TODO: expand/collapse
//                //--TODO: when click on this gpshandle_device_group, show map with all device in gpshandle_device_group
//            }
//        });

        TextView tvGrpID = (TextView) convertView.findViewById(R.id.listView_grpID);
        TextView tvGrpDesc = (TextView) convertView.findViewById(R.id.listView_grpDesc);
        tvGrpID.setText("["+groupID+"]");
        tvGrpDesc.setText(groupDescription);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            convertView = inflater.inflate(R.layout.gpshandle_device, null);
        }
        //-- deviceID and deviceDescription
        String deviceID     = "";
        String description  = "";
        String icon         = "";
        String address      = "";
        boolean isLive      = true;
        long lastEventTime  = 0;
        if (groups != null && devicesInGroup != null) {
            Device device = devicesInGroup.get(groups.get(groupPosition).getGroupId()).get(childPosition);
            deviceID = device.getDeviceID();
            description = device.getDescription();
            icon = device.getIcon();
            isLive = device.isLive();
            lastEventTime = device.getLastEventTime();
        }
        TextView deviceIDTextView = (TextView) convertView.findViewById(R.id.listView_deviceID);
        TextView deviceDescriptionTextView = (TextView) convertView.findViewById(R.id.listView_deviceDesc);
        deviceIDTextView.setText(deviceID);
        deviceDescriptionTextView.setText(description);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    //--------------------------------------------------------------------------------------------//
    //--End override
    //--------------------------------------------------------------------------------------------//

    //Getter/Setter
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public HashMap<String, ArrayList<Device>> getDevicesInGroup() {
        return devicesInGroup;
    }

    public void setGDevices(HashMap<String, ArrayList<Device>> gDevices) {
        this.devicesInGroup = gDevices;
    }
}

package com.umaps.gpshandleclient.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vu@umaps.vn on 01/11/2014.
 */
public class GroupExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Group> groups; //Header gpshandle_device_group
    private HashMap<String, ArrayList<Device>> devicesInGroup; //List of devices
    Typeface mTf = null;
    private ExpandableListView expl;

    public GroupExpandableListAdapter(Context _context, ArrayList<Group> _groups, HashMap<String, ArrayList<Device>> _gDevices){
        context = _context;
        groups = _groups;
        devicesInGroup = _gDevices;
        mTf = Typeface.createFromAsset(context.getAssets(), "icomoon.ttf");
    }

    public void setExpandableListView(ExpandableListView expandableListview){
        expl = expandableListview;
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
    public View getGroupView(final int groupPosition, boolean isExpandable, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            convertView = inflater.inflate(R.layout.gps_device_group, null);
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
            groupSize        = groups.get(groupPosition).getDeviceCount();
            countLive        = groups.get(groupPosition).getLive();
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.group_list_image);
        imageView.setImageResource(R.drawable.ic_directions_car_group_grey600_36dp);

        TextView tvGrpID = (TextView) convertView.findViewById(R.id.listView_grpID);
        TextView tvGrpDesc = (TextView) convertView.findViewById(R.id.listView_grpDesc);
        tvGrpID.setText("["+groupID+"]");
        tvGrpDesc.setText(groupDescription);

        //--set group_size
        TextView tvGroupSize = (TextView) convertView.findViewById(R.id.gps_group_size);
        tvGroupSize.setText(countLive+"/"+groupSize);

        final ImageButton btnExpand = (ImageButton) convertView.findViewById(R.id.btn_group_indicator);
        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expl.isGroupExpanded(groupPosition)){
                    expl.collapseGroup(groupPosition);
                    btnExpand.setImageResource(R.drawable.ic_expand_more_grey600_36dp);
                }else {
                    expl.expandGroup(groupPosition);
                    btnExpand.setImageResource(R.drawable.ic_expand_less_grey600_36dp);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            convertView = inflater.inflate(R.layout.gps_device, null);
        }
        //-- deviceID and deviceDescription
        String deviceID     = "";
        String description  = "";
        String icon         = "";
        String address      = "";
        boolean isLive      = true;
        double batteryLevel = 0D;
        long lastEventTime  = 0L;

        if (groups != null && devicesInGroup != null) {
            Device device = devicesInGroup.get(groups.get(groupPosition).getGroupId()).get(childPosition);
            deviceID = device.getDeviceID();
            description = device.getDescription();
            icon = device.getIcon();
            isLive = device.isLive();
            lastEventTime = device.getLastEventTime();
            batteryLevel = device.getBatteryLevel();
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.device_image);
        imageView.setImageResource(R.drawable.ic_directions_car_grey600_36dp);

        TextView deviceIDTextView = (TextView) convertView.findViewById(R.id.listView_deviceID);
        TextView deviceDescriptionTextView = (TextView) convertView.findViewById(R.id.listView_deviceDesc);
        deviceIDTextView.setText(deviceID);
        deviceDescriptionTextView.setText(description);
        TextView liveStateView  = (TextView) convertView.findViewById(R.id.device_status);
        TextView batteryView    = (TextView) convertView.findViewById(R.id.device_battery);
        TextView batteryText    = (TextView) convertView.findViewById(R.id.device_battery_text);
        liveStateView.setTypeface(mTf);
        batteryView.setTypeface(mTf);
        liveStateView.setText(String.valueOf((char)0xf111));
//        circleImageView.addShadow();
        /*CircleImageView circleImageView = (CircleImageView) convertView.findViewById(R.id.device_live_state);
        circleImageView.setBorderWidth(1);
        */
        if (isLive) {
            liveStateView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            liveStateView.setTextColor(context.getResources().getColor(R.color.red));
        }
        batteryView.setText(String.valueOf((char)0xe64c));
        batteryText.setText(String.format("%1$,.0f", batteryLevel*100)+"%");
        if (batteryLevel < 0.2){
            batteryView.setTextColor(context.getResources().getColor(R.color.red));
            batteryText.setTextColor(context.getResources().getColor(R.color.red));
        } else if ((batteryLevel < 0.7)) {
            batteryView.setTextColor(context.getResources().getColor(R.color.yellow));
            batteryText.setTextColor(context.getResources().getColor(R.color.yellow));
        }

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

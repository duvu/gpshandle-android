package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
public class DeviceGroupListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "DeviceGroupListAdapter";
    Typeface mTf = null;
    private Context context;
    private ArrayList<Group> groups;
    private HashMap<String, ArrayList<Device>> devicesInGroup;
    LayoutInflater mInflater;
    View gView;
    View dView;
    TextView txtIndicator;

    ExpandableListView expGroupList;

    public DeviceGroupListAdapter(Context context, ArrayList<Group> groups, HashMap<String, ArrayList<Device>> devicesInGroup){
        this.context = context;
        this.groups = groups;
        this.devicesInGroup = devicesInGroup;
        this.mTf = MyApplication.getIconFont();
        mInflater = LayoutInflater.from(context);
    }
    public void setExpGroupList(ExpandableListView expGroupList){
        this.expGroupList = expGroupList;
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
            gView = mInflater.inflate(R.layout.device_group, null);
        } else {
            gView = convertView;
        }

        if (groups == null) return  gView;

        String groupID          = groups.get(groupPosition).getGroupId();
        String groupDescription = groups.get(groupPosition).getDescription();
        String groupIcon        = groups.get(groupPosition).getIcon();
        int groupSize           = groups.get(groupPosition).getDeviceCount();
        int countLive           = groups.get(groupPosition).getLive();

        ImageView imageView = (ImageView) gView.findViewById(R.id.img_group);
        imageView.setImageResource(R.drawable.ic_directions_car_group_grey600_36dp);

        //TextView tvGrpID = (TextView) view.findViewById(R.id.listView_grpID);
        TextView tvGrpDesc = (TextView) gView.findViewById(R.id.txt_group_description);
        //tvGrpID.setText("["+groupID+"]");
        tvGrpDesc.setText(groupDescription);

        //--set group_size
        TextView tvGroupSize = (TextView) gView.findViewById(R.id.gps_group_size);
        tvGroupSize.setText(countLive + "/" + groupSize);

        txtIndicator = (TextView) gView.findViewById(R.id.ic_indicator);
        txtIndicator.setTypeface(mTf);
        txtIndicator.setText(String.valueOf((char) 0xe6ee));
        txtIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expGroupList.isGroupExpanded(groupPosition)) {
                    expGroupList.collapseGroup(groupPosition);
                    txtIndicator.setText(String.valueOf((char) 0xe6ed));
                } else {
                    expGroupList.expandGroup(groupPosition, true);
                    txtIndicator.setText(String.valueOf((char) 0xe6ee));
                }
            }
        });
        return gView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            dView = mInflater.inflate(R.layout.device, null);
        } else {
            dView = convertView;
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

        View devState = dView.findViewById(R.id.ic_lv_device_status);
        if (isLive) {
            devState.setBackgroundColor(this.context.getResources().getColor(R.color.green));
        } else {
            devState.setBackgroundColor(this.context.getResources().getColor(R.color.bad));
        }

        TextView deviceIDTextView = (TextView) dView.findViewById(R.id.txt_lv_device_id);
        deviceIDTextView.setText(deviceID);
        TextView deviceDescriptionTextView = (TextView) dView.findViewById(R.id.txt_lv_device_description);
        deviceDescriptionTextView.setText(description);

        TextView batteryView    = (TextView) dView.findViewById(R.id.ic_device_battery);
        TextView batteryText    = (TextView) dView.findViewById(R.id.txt_device_battery);
        batteryView.setTypeface(mTf);
        batteryView.setText(String.valueOf((char)0xe659));
        batteryText.setText(padRight(batteryLevel *100)+"%");
        if (batteryLevel <= 0.2){
            batteryView.setTextColor(context.getResources().getColor(R.color.bad));
            batteryText.setTextColor(context.getResources().getColor(R.color.bad));
        } else if ((batteryLevel <= 0.7)) {
            batteryView.setTextColor(context.getResources().getColor(R.color.warning));
            batteryText.setTextColor(context.getResources().getColor(R.color.warning));
        } else {
            batteryView.setTextColor(context.getResources().getColor(R.color.green));
            batteryText.setTextColor(context.getResources().getColor(R.color.green));
        }
        return dView;
    }
    public String padRight(double s) {
        return String.format("%1$,.0f", s);
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    //--------------------------------------------------------------------------------------------//
    //--End override
    //--------------------------------------------------------------------------------------------//
}

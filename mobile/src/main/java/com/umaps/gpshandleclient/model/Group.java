package com.umaps.gpshandleclient.model;

import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by beou on 31/10/2014.
 */
public class Group {

    private static final String ACCOUNT_ID          = "accountID";
    private static final String GROUP_ID            = "groupID";
    private static final String DESCRIPTION         = "description";
    private static final String DISPLAY_NAME        = "displayName";
    private static final String DEVICE_COUNT        = "deviceCount";
    private static final String DEVICE_LIST         = "devicesList";
    private static final String PUSHPIN_ID          = "pushpinID";
    private static final String NOTES               = "notes";
    private static final String LAST_UPDATE_TIME    = "lastUpdateTime";
    private static final String CREATION_TIME       = "creationTime";

    private String accountId;
    private String groupId;
    private String description;
    private String displayName;
    private long lastUpdateTime;
    private long creationTime;

    private String icon;
    private int live = 0;
    private int deviceCount = 0;

    private ArrayList<Device> deviceArrayList;

    public Group(JSONObject itemGroup){
        try {
            this.accountId      = (itemGroup.has(ACCOUNT_ID) ? itemGroup.getString(ACCOUNT_ID) : "");
            this.groupId        = (itemGroup.has(GROUP_ID) ? itemGroup.getString(GROUP_ID) : "");
            this.description    = (itemGroup.has(DESCRIPTION) ? itemGroup.getString(DESCRIPTION) : "");
            this.displayName    = itemGroup.has(DISPLAY_NAME) ? itemGroup.getString(DISPLAY_NAME) : "";
            this.lastUpdateTime = itemGroup.has(LAST_UPDATE_TIME) ? itemGroup.getLong(LAST_UPDATE_TIME) : 0L;
            this.creationTime = itemGroup.has(CREATION_TIME) ? itemGroup.getLong(CREATION_TIME) : 0L;
            this.deviceCount    = itemGroup.has(DEVICE_COUNT) ? itemGroup.getInt(DEVICE_COUNT) : 0;
            if (itemGroup.has(DEVICE_LIST)){
                deviceArrayList = new ArrayList<Device>();
                JSONArray dA = itemGroup.getJSONArray(DEVICE_LIST);
                for (int i = 0; i< dA.length(); i++){
                    Device d = new Device(dA.getJSONObject(i));
                    deviceArrayList.add(d);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Group(String accountID, String groupId, String description,
                 String displayName, String icon, int live, int deviceCount){
        this.setAccountId(accountID);
        this.setGroupId(groupId);
        this.setDescription(description);
        this.setDisplayName(displayName);
        this.setIcon(icon);
        this.setLive(live);
        this.setDeviceCount(deviceCount);
    }
    public String getAccountId(){
        return (accountId!=null)?accountId:"";
    }
    public void setAccountId(String accountID){
        this.accountId = accountID;
    }
    public String getGroupId() {
        return (groupId!=null)?groupId:"";
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return (!StringTools.isBlank(description))?description:displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDisplayName(){
        return (displayName!=null)?displayName:description;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }
    public int getDeviceCount() {
        return deviceCount;
    }
    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public ArrayList<Device> getDeviceArrayList() {
        return deviceArrayList;
    }

    public void setDeviceArrayList(ArrayList<Device> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }

    public static JSONObject createGetParams(){
        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<>();
        fields.add(ACCOUNT_ID);
        fields.add(GROUP_ID);
        fields.add(DESCRIPTION);
        fields.add(PUSHPIN_ID);
        fields.add(DISPLAY_NAME);
        fields.add(DEVICE_COUNT);
        fields.add(DEVICE_LIST);
        fields.add(NOTES);
        fields.add(LAST_UPDATE_TIME);
        fields.add(CREATION_TIME);
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParamsObject;
    }
}

package com.umaps.gpssdk.model;

import java.util.List;

/**
 * Created by beou on 31/10/2014.
 */
public class Group extends GObject {

    private static final String ACCOUNT_ID          = "accountID";
    private static final String GROUP_ID            = "groupID";
    private static final String DESCRIPTION         = "description";
    private static final String DISPLAY_NAME        = "displayName";
    private static final String DEVICE_COUNT        = "deviceCount";
    private static final String DEVICE_LIST         = "devicesList";
    private static final String NOTES               = "notes";
    private static final String LAST_UPDATE_TIME    = "lastUpdateTime";
    private static final String CREATION_TIME       = "creationTime";

    public void setAccount(String accountId) {
        put(ACCOUNT_ID, accountId);
    }
    public String getAccount () {
        return getString(ACCOUNT_ID);
    }
    public void setGroupId(String groupId) {
        put(GROUP_ID, groupId);
    }
    public String getGroupId() {
        return getString(GROUP_ID);
    }
    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }
    public String getDescription() {
        return getString(DESCRIPTION);
    }
    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }
    public String getDisplayName () {
        return getString(DISPLAY_NAME);
    }

    public void setDeviceCount(int count) {
        put(DEVICE_COUNT, count);
    }
    public int getDeviceCount() {
        return getInt(DEVICE_COUNT);
    }
    public void setDeviceList(List<GObject> list) {
        put(DEVICE_LIST, list);
    }
    public List<GObject> getDeviceList() {
        return getObjects(DEVICE_LIST);
    }

    public Group(String accountID, String groupId, String description,
                 String displayName, String icon, int live, int deviceCount){
        this.setAccount(accountID);
        this.setGroupId(groupId);
        this.setDescription(description);
        this.setDisplayName(displayName);
        //this.setIcon(icon);
        //this.setLive(live);
        this.setDeviceCount(deviceCount);
    }
    /*public String getAccountId(){
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<Device> getDeviceArrayList() {
        return deviceArrayList;
    }

    public void setDeviceArrayList(ArrayList<Device> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }*/


    /*public static JSONObject createGetParams(){
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

    private JSONObject buildParams(){
        JSONObject j = new JSONObject();
        try {
            j.put(GROUP_ID, this.groupId);
            j.put(DESCRIPTION, this.description);
            j.put(DISPLAY_NAME, this.displayName);
            j.put(NOTES, this.notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j;
    }

    public GpsRequest getRequestCreate(){
        if (this.context == null) return null;
        GpsRequest r = new GpsRequest();
        r.setAccountID(GpsSdk.getAccountId());
        r.setUserID(GpsSdk.getUserId());
        r.setPassword(GpsSdk.getUserPassword());
        r.setCommand(GpsRequest.CMD_CREATE_GROUP);
        r.setMethod(Request.Method.POST);
        r.setUrl(GpsRequest.ADMIN_URL);
        r.setParams(buildParams());
        return r;
    }

    public GpsRequest getRequestEdit() {
        if (this.context == null) return null;

        GpsRequest r = new GpsRequest();
        r.setAccountID(GpsSdk.getAccountId());
        r.setUserID(GpsSdk.getUserId());
        r.setPassword(GpsSdk.getUserPassword());
        r.setCommand(GpsRequest.CMD_UPDATE_GROUP);
        r.setMethod(Request.Method.POST);
        r.setUrl(GpsRequest.ADMIN_URL);
        r.setParams(buildParams());
        return r;
    }
    public GpsRequest getRequestDelete() {
        if (this.context == null) return null;

        GpsRequest r = new GpsRequest();
        r.setAccountID(GpsSdk.getAccountId());
        r.setUserID(GpsSdk.getUserId());
        r.setPassword(GpsSdk.getUserPassword());
        r.setCommand(GpsRequest.CMD_DELETE_GROUP);
        r.setMethod(Request.Method.POST);
        r.setUrl(GpsRequest.ADMIN_URL);
        r.setParams(buildParams());
        return r;
    }*/
}

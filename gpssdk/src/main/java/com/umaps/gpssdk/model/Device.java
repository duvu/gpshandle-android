package com.umaps.gpssdk.model;

import android.content.Context;

import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.Query;
import com.umaps.gpssdk.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by beou on 01/11/2014.
 */
public class Device {

    private static final String ACCOUNT_ID          = "accountID";
    private static final String DEVICE_ID           = "deviceID";
    private static final String DESCRIPTION         = "description";
    private static final String DISPLAY_NAME        = "displayName";
    private static final String UNIQUE_ID           = "uniqueID";
    private static final String GROUP_ID            = "groupID";
    private static final String DRIVER_ID           = "driverID";
    private static final String DRIVER_STATUS       = "driverStatus";
    private static final String PUSHPIN_ID          = "pushpinID";
    private static final String NOTES               = "notes";
    private static final String SERIAL_NUMBER       = "serialNumber";
    private static final String SIM_NUMBER          = "simNumber";
    private static final String LAST_LATITUDE       = "lastValidLatitude";
    private static final String LAST_LONGITUDE      = "lastValidLongitude";

    private static final String IS_ACTIVE           = "isActive";
    private static final String SMS_COUNT           = "smsCount";
    private static final String SMS_LIMIT           = "smsMonthlyLimited";
    private static final String CREATION_TIME       = "creationTime";

    private static final String ICON                = "pushPin";
    private static final String IS_LIVE             = "isLive";
    private static final String LAST_EVENT_TIME     = "lastEventTime";
    private static final String LAST_BATTERY_LEVEL  = "lastBatteryLevel";


    private Context context;

    private String accountID;
    private String deviceID;
    private String description;
    private String displayName;
    private String uniqueID;
    private String groupIDs;
    private String driverID;
    private int driverStatus;
    private String pushpinID;
    private String notes;
    private String serialNumber;
    private String simNumber;
    private double lastLatitude;
    private double lastLongitude;

    private int smsCount;
    private int smsLimit;
    private long creationTime;

    private String icon;
    private boolean isActive;
    private boolean isLive = true;
    private long   lastEventTime;
    private double lastBatteryLevel;

    public Device(JSONObject item) {
        if (item == null) return;
        try {
            this.accountID      = item.has(ACCOUNT_ID) ? item.getString(ACCOUNT_ID) : "";
            this.deviceID       = item.has(DEVICE_ID) ? item.getString(DEVICE_ID) : "";
            this.description    = item.has(DESCRIPTION) ? item.getString(DESCRIPTION) : "";
            this.displayName    = item.has(DISPLAY_NAME) ? item.getString(DISPLAY_NAME) : "";
            this.uniqueID       = item.has(UNIQUE_ID) ? item.getString(UNIQUE_ID) : "";
            this.groupIDs       = item.has(GROUP_ID) ? item.getString(GROUP_ID) : "";
            this.driverID       = item.has(DRIVER_ID) ? item.getString(DRIVER_ID) : "";
            this.driverStatus   = item.has(DRIVER_STATUS) ? item.getInt(DRIVER_STATUS) : 0;
            this.pushpinID      = item.has(PUSHPIN_ID) ? item.getString(PUSHPIN_ID) : "";
            this.notes          = item.has(NOTES) ? item.getString(NOTES) : "";
            this.serialNumber   = item.has(SERIAL_NUMBER) ? item.getString(SERIAL_NUMBER) : "";
            this.simNumber = item.has(SIM_NUMBER) ? item.getString(SIM_NUMBER) : "";
            this.lastLatitude   = item.has(LAST_LATITUDE) ? item.getDouble(LAST_LATITUDE) : 0.0D;
            this.lastLongitude  = item.has(LAST_LONGITUDE) ? item.getDouble(LAST_LONGITUDE) : 0.0D;

            this.isActive       = item.has(IS_ACTIVE) ? item.getBoolean(IS_ACTIVE) : false;
            this.smsCount       = item.has(SMS_COUNT) ? item.getInt(SMS_COUNT) : 0;
            this.smsLimit       = item.has(SMS_LIMIT) ? item.getInt(SMS_LIMIT) : 0;
            this.creationTime   = item.has(CREATION_TIME) ? item.getLong(CREATION_TIME) : 0L;

            this.icon           = item.has(ICON) ? item.getString(ICON) : "";
            this.lastEventTime   = item.has(LAST_EVENT_TIME) ? item.getLong(LAST_EVENT_TIME) : 0L;
            long currTimestamp = Calendar.getInstance().getTimeInMillis() / 1000;
            this.isLive         = (isActive && (currTimestamp - lastEventTime < 300));
            this.lastBatteryLevel = item.has(LAST_BATTERY_LEVEL) ? item.getDouble(LAST_BATTERY_LEVEL) : 0D;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Device(){}
    public Device(Context context){
        this.context = context;
    }

    public Device(String deviceID, String description) {
        this(deviceID, description, Calendar.getInstance().getTimeInMillis() / 1000, null);
    }
    public Device(String devID, String desc, long lastEventTime, String address){
        this(devID, desc, null, true, lastEventTime);
    }
    public Device(String deviceID, String description, String icon, boolean isLive, long lastEventTime){
        this.setDeviceID(deviceID);
        this.setDescription(description);
        this.setIcon(icon);
        this.setLive(isLive);
        this.setLastEventTime(lastEventTime);
    }
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDisplayName() {
        return (StringTools.isBlank(displayName)?getDescription():displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDescription() {
        String t = (StringTools.isBlank(description) ? displayName : description);
        return (StringTools.isBlank(t)?getDeviceID():t);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }

    public long getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(long lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public boolean isActive(){
        return isActive;
    }
    public void setActive(boolean active){
        this.isActive = active;
    }

    public double getLastBatteryLevel() {
        return lastBatteryLevel;
    }

    public void setLastBatteryLevel(double batteryLevel) {
        this.lastBatteryLevel = batteryLevel;
    }


    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getGroupIDs() {
        return groupIDs;
    }

    public void setGroupIDs(String groupIDs) {
        this.groupIDs = groupIDs;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public int getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public String getPushpinID() {
        return pushpinID;
    }

    public void setPushpinID(String pushpinID) {
        this.pushpinID = pushpinID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
    }
    public String getSimNumber() {
        return this.simNumber;
    }

    public int getSmsLimit() {
        return smsLimit;
    }

    public void setSmsLimit(int smsLimit) {
        this.smsLimit = smsLimit;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static JSONObject getParams(){

        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<>();
        fields.add(ACCOUNT_ID);
        fields.add(DEVICE_ID);
        fields.add(DESCRIPTION);
        fields.add(DISPLAY_NAME);
        fields.add(UNIQUE_ID);
        fields.add(GROUP_ID);
        fields.add(DRIVER_ID);
        fields.add(DRIVER_STATUS);
        fields.add(PUSHPIN_ID);
        fields.add(NOTES);
        fields.add(SERIAL_NUMBER);
        fields.add(LAST_LATITUDE);
        fields.add(LAST_LONGITUDE);
        fields.add(IS_ACTIVE);
        fields.add(SMS_COUNT);
        fields.add(SMS_LIMIT);
        fields.add(CREATION_TIME);
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParamsObject;
    }

    public Query getRequestCreate(){
        Query crtQuery = new Query();
        crtQuery.setAccountID(GpsSdk.getAccountId());
        crtQuery.setUserID(GpsSdk.getUserId());
        crtQuery.setPassword(GpsSdk.getUserPassword());
//        crtRequest.setLocale(myApplication.getLocale());
        crtQuery.setUrl(Query.ADMIN_URL);
        crtQuery.setMethod(com.android.volley.Request.Method.POST);
        crtQuery.setCommand(Query.CMD_CREATE_DEVICE);
        crtQuery.setParams(buildParams());
        return crtQuery;
    }

    public Query getRequestEdit() {
        Query edtQuery = new Query();
        edtQuery.setAccountID(GpsSdk.getAccountId());
        edtQuery.setUserID(GpsSdk.getUserId());
        edtQuery.setPassword(GpsSdk.getUserPassword());
//        edtRequest.setLocale(myApplication.getLocale());
        edtQuery.setUrl(Query.ADMIN_URL);
        edtQuery.setMethod(com.android.volley.Request.Method.POST);
        edtQuery.setParams(buildParams());
        edtQuery.setCommand(Query.CMD_UPDATE_DEVICE);
        return edtQuery;
    }

    public Query getRequestDelete() {
        Query deleteQuery = new Query();
        deleteQuery.setAccountID(GpsSdk.getAccountId());
        deleteQuery.setUserID(GpsSdk.getUserId());
        deleteQuery.setPassword(GpsSdk.getUserPassword());
//        deleteRequest.setLocale(myApplication.getLocale());
        deleteQuery.setUrl(Query.ADMIN_URL);
        deleteQuery.setMethod(com.android.volley.Request.Method.POST);
        deleteQuery.setParams(buildParams());
        deleteQuery.setCommand(Query.CMD_DELETE_DEVICE);
        return deleteQuery;
    }

    private JSONObject buildParams(){
        JSONObject params = new JSONObject();
        if (StringTools.isBlank(this.uniqueID)) return null;
        try {
            params.put(UNIQUE_ID, this.uniqueID);
            params.put(DEVICE_ID, this.deviceID);
            params.put(DESCRIPTION, this.description);
            params.put(DISPLAY_NAME, this.displayName);
            params.put(IS_ACTIVE, this.isActive);
            params.put(SERIAL_NUMBER, this.serialNumber);
            params.put(SIM_NUMBER, this.simNumber);
            params.put(SMS_LIMIT, this.smsLimit);
            params.put(GROUP_ID, this.groupIDs);
            params.put(NOTES, this.notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}

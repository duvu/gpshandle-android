package com.umaps.gpshandleclient.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 20/08/2015.
 */
@ParseClassName("ParseDevice")
public class ParseDevice extends ParseObject {
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
    private static final String LAST_EVENT_TIME     = "lastEventTime";
    private static final String LAST_BATTERY_LEVEL  = "lastBatteryLevel";

    public ParseDevice() {}

    public static ParseDevice parseDevice(JSONObject j) throws JSONException {
        ParseDevice p = new ParseDevice();
        if (j.has(ACCOUNT_ID))          {p.setAccountId     (j.getString(ACCOUNT_ID));}
        if (j.has(DEVICE_ID))           {p.setDeviceId      (j.getString(DEVICE_ID));}
        if (j.has(DESCRIPTION))         {p.setDescription   (j.getString(DESCRIPTION));}
        if (j.has(DISPLAY_NAME))        {p.setDisplayName   (j.getString(DISPLAY_NAME));}
        if (j.has(UNIQUE_ID))           {p.setUniqueId      (j.getString(UNIQUE_ID));}
        if (j.has(GROUP_ID))            {p.setGroupId       (j.getString(GROUP_ID));}
        if (j.has(DRIVER_ID))           {p.setDriverId      (j.getString(DRIVER_ID));}
        if (j.has(DRIVER_STATUS))       {p.setDriverStatus  (j.getInt(DRIVER_STATUS));}
        if (j.has(PUSHPIN_ID))          {p.setPushpinId     (j.getString(PUSHPIN_ID));}
        if (j.has(NOTES))               {p.setNotes         (j.getString(PUSHPIN_ID));}
        if (j.has(SERIAL_NUMBER))       {p.setSerialNumber  (j.getString(SERIAL_NUMBER));}
        if (j.has(SIM_NUMBER))          {p.setSimNumber     (j.getString(SIM_NUMBER));}
        if (j.has(LAST_LATITUDE))       {p.setLastLatitude  (j.getDouble(LAST_LATITUDE));}
        if (j.has(LAST_LONGITUDE))      {p.setLastLongitude (j.getDouble(LAST_LONGITUDE));}
        if (j.has(IS_ACTIVE))           {p.setActive        (j.getBoolean(IS_ACTIVE));}
        if (j.has(SMS_COUNT))           {p.setSmsCount      (j.getInt(SMS_COUNT));}
        if (j.has(SMS_LIMIT))           {p.setSmsLimit      (j.getInt(SMS_LIMIT));}
        if (j.has(CREATION_TIME))       {p.setCreationTime  (j.getLong(CREATION_TIME));}
        if (j.has(LAST_EVENT_TIME))     {p.setLastEventTime (j.getLong(LAST_EVENT_TIME));}
        if (j.has(LAST_BATTERY_LEVEL))  {p.setBatteryLevel  (j.getDouble(LAST_BATTERY_LEVEL));}

        return p;
    }

    public String getAccountId() {
        return getString(ACCOUNT_ID);
    }
    public void setAccountId(String accountId) {
        put(ACCOUNT_ID, accountId);
    }

    public String getDeviceId () {
        return getString(DEVICE_ID);
    }
    public void setDeviceId(String deviceId) {
        put(DEVICE_ID, deviceId);
    }
    public String getDescription() {
        return getString(DESCRIPTION);
    }
    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }
    public String getDisplayName() {
        return getString(DISPLAY_NAME);
    }
    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }
    public String getUniqueId() {
        return getString(UNIQUE_ID);
    }
    public void setUniqueId(String uniqueId) {
        put(UNIQUE_ID, uniqueId);
    }
    public String getGroupId() {
        return getString(GROUP_ID);
    }
    public void setGroupId(String groupId) {
        put(GROUP_ID, groupId);
    }

    public String getDriverId() {
        return getString(DRIVER_ID);
    }
    public void setDriverId(String driverId) {
        put(DRIVER_ID, driverId);
    }
    public int getDriverStatus () {
        return getInt(DRIVER_STATUS);
    }
    public void setDriverStatus(int driverStatus) {
        put(DRIVER_STATUS, driverStatus);
    }
    public String getPushpinId() {
        return getString(PUSHPIN_ID);
    }
    public void setPushpinId(String pushpinId) {
        put(PUSHPIN_ID, pushpinId);
    }
    public String getNotes() {
        return getString(NOTES);
    }
    public void setNotes(String notes) {
        put(NOTES, notes);
    }
    public String getSerialNumber() {
        return getString(SERIAL_NUMBER);
    }
    public void setSerialNumber(String serialNumber) {
        put(SERIAL_NUMBER, serialNumber);
    }
    public String getSimNumber() {
        return getString(SIM_NUMBER);
    }
    public void setSimNumber(String simNumber) {
        put(SIM_NUMBER, simNumber);
    }
    public double getLastLatitude() {
        return getDouble(LAST_LATITUDE);
    }
    public void setLastLatitude(double lastLatitude) {
        put(LAST_LATITUDE, lastLatitude);
    }
    public double getLastLongitude() {
        return getDouble(LAST_LONGITUDE);
    }
    public void setLastLongitude(double longitude) {
        put(LAST_LONGITUDE, longitude);
    }

    public boolean isActive() {
        return getBoolean(IS_ACTIVE);
    }
    public void setActive(boolean active) {
        put(IS_ACTIVE, active);
    }

    public int getSmsCount() {
        return getInt(SMS_COUNT);
    }
    public void setSmsCount(int count) {
        put(SMS_COUNT, count);
    }
    public void incrSmsCount() {
        increment(SMS_COUNT);
    }

    public int getSmsLimit() {
        return getInt(SMS_LIMIT);
    }
    public void setSmsLimit(int smsLimit) {
        put(SMS_LIMIT, smsLimit);
    }
    public long getCreationTime() {
        return getLong(CREATION_TIME);
    }
    public void setCreationTime(long creationTime) {
        put(CREATION_TIME, creationTime);
    }

    public long getLastEventTime() {
        return getLong(LAST_EVENT_TIME);
    }
    public void setLastEventTime(long lastEventTime) {
        put(LAST_EVENT_TIME, lastEventTime);
    }

    public double getBatteryLevel() {
        return getDouble(LAST_BATTERY_LEVEL);
    }
    public void setBatteryLevel(double level) {
        put(LAST_BATTERY_LEVEL, level);
    }
}

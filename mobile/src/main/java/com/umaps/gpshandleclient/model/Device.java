package com.umaps.gpshandleclient.model;

import java.util.Calendar;

/**
 * Created by beou on 01/11/2014.
 */
public class Device {
    private String deviceID;
    private String description;
    private String icon;
//    private String address;
    private boolean isLive = true;
    private long   lastEventTime;

    public Device(String deviceID, String description){
        this(deviceID, description, Calendar.getInstance().getTimeInMillis()/1000, null);
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

    public String getDescription() {
        return description;
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

}

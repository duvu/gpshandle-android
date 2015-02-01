package com.umaps.gpshandleclient.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by beou on 01/11/2014.
 */
public class Device {
    private String deviceID;
    private String description;
    private String address;
    private long   lastEventTime;

    public Device(String deviceID, String description){
        this(deviceID, description, Calendar.getInstance().getTimeInMillis()/1000, null);
    }
    public Device(String devID, String desc, long lastEventTime, String address){
        this.setDeviceID(devID);
        this.setDescription(desc);
        this.setLastEventTime(lastEventTime);
        this.setAddress(address);
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

    public long getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(long lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

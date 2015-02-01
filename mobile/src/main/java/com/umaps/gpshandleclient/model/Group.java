package com.umaps.gpshandleclient.model;

import java.util.ArrayList;

/**
 * Created by beou on 31/10/2014.
 */
public class Group {
    private String groupid;
    private String description;
    private int numDevices;
    public Group(String grpid, String desc, int numdev){
        this.setGroupid(grpid);
        this.setDescription(desc);
        this.setNumDevices(numdev);
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumDevices() {
        return numDevices;
    }

    public void setNumDevices(int numDevices) {
        this.numDevices = numDevices;
    }
}

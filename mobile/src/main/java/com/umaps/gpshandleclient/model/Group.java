package com.umaps.gpshandleclient.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by beou on 31/10/2014.
 */
public class Group {
    private String accountId;
    private String groupId;
    private String description;
    private String displayName;
    private String icon;
    private int live = 0;
    private int deviceCount = 0;

    public Group(JSONObject itemGroup){

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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDisplayName(){
        return (displayName!=null)?displayName:"";
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

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }
}

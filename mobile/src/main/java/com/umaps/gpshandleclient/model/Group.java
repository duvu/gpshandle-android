package com.umaps.gpshandleclient.model;

/**
 * Created by beou on 31/10/2014.
 */
public class Group {
    private String groupId;
    private String icon;
    private String description;
    private int live = 0;
    private int count = 0;
//    public Group(String groupId, String desc, int count){
//        this.setGroupId(groupId);
//        this.setDescription(desc);
//        this.setCount(count);
//    }
    public Group(String groupId, String description, String icon, int live, int count){
        this.setGroupId(groupId);
        this.setIcon(icon);
        this.setDescription(description);
        this.setLive(live);
        this.setCount(count);
    }
    public String getGroupId() {
        return groupId;
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

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }
}

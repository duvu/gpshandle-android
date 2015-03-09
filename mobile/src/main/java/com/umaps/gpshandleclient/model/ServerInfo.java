package com.umaps.gpshandleclient.model;

/**
 * Created by vu@umaps.vn on 13/02/2015.
 */
public class ServerInfo {
    private String trackingUrl;
    private String webUrl;
    private String name;
    private String ipAddress;
    private int cpuCapacity;    //numOfCore
    private int ramCapacity;    //GiB

    public ServerInfo(String url, String wUrl, String name, String ipAddress, int cpuCapacity, int ramCapacity){
        this.trackingUrl = url;
        this.webUrl = wUrl;
        this.name = name;
        this.ipAddress = ipAddress;
        this.cpuCapacity = cpuCapacity;
        this.ramCapacity = ramCapacity;
    }
    public ServerInfo(String url, String wUrl, String name, String ipAddress){
        this(url, wUrl, name, ipAddress, 0, 0);
    }
    public ServerInfo(String url, String wUrl, String name){
        this(url, wUrl, name, null, 0, 0);
    }

    public String getTrackingUrl(){
        return trackingUrl;
    }

    public void setTrackingUrl(String url){
        this.trackingUrl = url;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String url) {
        this.webUrl = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getCpuCapacity() {
        return cpuCapacity;
    }

    public void setCpuCapacity(int cpuCapacity) {
        this.cpuCapacity = cpuCapacity;
    }

    public int getRamCapacity() {
        return ramCapacity;
    }

    public void setRamCapacity(int ramCapacity) {
        this.ramCapacity = ramCapacity;
    }

    @Override
    public String toString(){
        return this.name;
    }
}

package com.umaps.gpshandleclient.model;

import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 04/06/2015.
 */
public class MapPoint {
    public static final String SEP = "|";

    private String  id;
    private String  vin;
    private String  desc;
    private long    epoch;
    private String  date;
    private String  time;
    private String  timeZone;
    private String  status;
    private String  icon;
    private double  latitude;
    private double  longitude;
    private long    gpsAge;
    private long    creationAge;
    private double  accuracy;
    private int     numSat;
    private double  speedKPH;
    private double  heading;
    private double  altitude;
    private double  odoM;
    private boolean stopped;
    private String  gpio;
    private String  address;
    private double  batteryLevel;
    private double  ssi;
    public MapPoint(String data){
        if (StringTools.isBlank(data)){ return; }
        String[] _data = data.split("\\|", -1);
        this.setId          (_data[0]);
        this.setVin         (_data[1]);
        this.setDesc        (_data[2]);
        this.setEpoch       (Long.parseLong(_data[3]));
        this.setDate        (_data[4]);
        this.setTime        (_data[5]);
        this.setTimeZone    (_data[6]);
        this.setStatus      (_data[7]);
        this.setIcon        (_data[8]);
        this.setLatitude    (Double.parseDouble(_data[9]));
        this.setLongitude   (Double.parseDouble(_data[10]));
        this.setGpsAge      (Long.parseLong(_data[11]));
        this.setCreationAge (Long.parseLong(_data[12]));
        this.setAccuracy    (Double.parseDouble(_data[13]));
        this.setNumSat      (Integer.parseInt(_data[14]));
        this.setSpeedKPH    (Double.parseDouble(_data[15]));
        this.setHeading     (Double.parseDouble(_data[16]));
        this.setAltitude    (Double.parseDouble(_data[17]));
        this.setOdoM        (Double.parseDouble(_data[18]));
        this.setStopped     (Boolean.parseBoolean(_data[19]));
        this.setGpio        (_data[20]);
        this.setAddress     (_data[21]);
        this.setBatteryLevel(Double.parseDouble(_data[22]));
        this.setSsi         (Double.parseDouble(_data[23]));
    }
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(id)           .append(SEP)    .append(vin)         .append(SEP);
        sb.append(desc)         .append(SEP)    .append(epoch)       .append(SEP);
        sb.append(date)         .append(SEP)    .append(time)        .append(SEP);
        sb.append(timeZone)     .append(SEP)    .append(status)      .append(SEP);
        sb.append(icon)         .append(SEP)    .append(latitude)    .append(SEP);
        sb.append(longitude)    .append(SEP)    .append(gpsAge)      .append(SEP);
        sb.append(creationAge)  .append(SEP)    .append(accuracy)    .append(SEP);
        sb.append(numSat)       .append(SEP)    .append(speedKPH)    .append(SEP);
        sb.append(heading)      .append(SEP)    .append(altitude)    .append(SEP);
        sb.append(odoM)         .append(SEP)    .append(stopped)     .append(SEP);
        sb.append(gpio)         .append(SEP)    .append(address)     .append(SEP);
        sb.append(batteryLevel) .append(SEP)    .append(ssi);
        return sb.toString();
    }
    //-- getter
    public String   getId()          { return id; }
    public String   getVin()         { return vin; }
    public String   getDesc()        { return desc; }
    public long     getEpoch()       { return epoch; }
    public String   getDate()        { return date; }
    public String   getTime()        { return time; }
    public String   getTimeZone()    { return timeZone; }
    public String   getStatus()      { return status; }
    public String   getIcon()        { return this.icon; }
    public double   getLatitude()    { return latitude; }
    public double   getLongitude()   { return longitude; }
    public long     getGpsAge()      { return gpsAge; }
    public long     getCreationAge() { return creationAge; }
    public double   getAccuracy()    { return accuracy; }
    public int      getNumSat()      { return numSat; }
    public double   getSpeedKPH()    { return speedKPH; }
    public double   getHeading()     { return heading; }
    public double   getAltitude()    { return altitude; }
    public double   getOdoM()        { return odoM; }
    public boolean  isStopped()      { return stopped; }
    public String   getGpio()        { return gpio; }
    public String   getAddress()     { return address; }
    public double   getBatteryLevel(){ return batteryLevel;}
    public double   getSsi()         { return ssi; }

    //--setter
    public void     setId(String id)                    { this.id = id; }
    public void     setVin(String vin)                  { this.vin = vin; }
    public void     setDesc(String desc)                { this.desc = desc; }
    public void     setEpoch(long epoch)                { this.epoch = epoch; }
    public void     setDate(String date)                { this.date = date; }
    public void     setTime(String time)                { this.time = time; }
    public void     setTimeZone(String timeZone)        { this.timeZone = timeZone; }
    public void     setStatus(String status)            { this.status = status; }
    public void     setIcon(String icon)                { this.icon = icon; }
    public void     setLatitude(double latitude)        { this.latitude = latitude; }
    public void     setLongitude(double longitude)      { this.longitude = longitude; }
    public void     setGpsAge(long gpsAge)              { this.gpsAge = gpsAge; }
    public void     setCreationAge(long creationAge)    { this.creationAge = creationAge; }
    public void     setAccuracy(double accuracy)        { this.accuracy = accuracy; }
    public void     setNumSat(int numSat)               { this.numSat = numSat; }
    public void     setSpeedKPH(double speedKPH)        { this.speedKPH = speedKPH; }
    public void     setHeading(double heading)          { this.heading = heading; }
    public void     setAltitude(double altitude)        { this.altitude = altitude; }
    public void     setOdoM(double odoM)                { this.odoM = odoM; }
    public void     setStopped(boolean stopped)         { this.stopped = stopped; }
    public void     setGpio(String gpio)                { this.gpio = gpio; }
    public void     setAddress(String address)          { this.address = address; }
    public void     setBatteryLevel(double v)           { this.batteryLevel = v; }
    public void     setSsi(double v)                    { this.ssi = v; }
}

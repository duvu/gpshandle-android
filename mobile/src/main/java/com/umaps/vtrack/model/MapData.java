package com.umaps.vtrack.model;


import com.umaps.vtrack.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beou on 29/10/2014.
 */
public class MapData {
    public static final String MD_IS_CLUSTER        = "isCluster";
    public static final String MD_DAtA              = "data";
    public static final String MD_RUNNING_COUNT     = "runningCount";
    public static final String MD_AVERAGE_SPEED     = "averageSpeed";
    public static final String MD_DEVICE_LIST       = "deviceList";
    public static final String MD_COUNT             = "count";

    public class Point{
        public static final String MD_ID           = "id";
        public static final String MD_VIN          = "vin";
        public static final String MD_DESC         = "desc";
        public static final String MD_EPOCH        = "epoch";
        public static final String MD_STATUS       = "status";
        public static final String MD_LATITUDE     = "latitude";
        public static final String MD_LONGITUDE    = "longitude";
        public static final String MD_GPS_AGE      = "gpsAge";
        public static final String MD_CREATION_AGE = "creationAge";
        public static final String MD_ACCURACY     = "accuracy";
        public static final String MD_SAT_COUNT    = "satCount";
        public static final String MD_SPEED        = "speed";
        public static final String MD_HEADING      = "heading";
        public static final String MD_ALTITUDE     = "altitude";
        public static final String MD_ODOM         = "odom";
        public static final String MD_IS_STOPPED   = "isStopped";
        public static final String MD_GP_IO        = "gpIO";
        public static final String MD_ADDRESS      = "address";
        //----------------------------------------------------------------------------------------//
        private String id;
        private String vin;
        private String deviceDescription;
        private long epoch;
        private String statusCodeDesc;
        private double latitude;
        private double longitude;
        private long gpsAge;
        private long creationAge;
        private double accuracy;
        private int numSat;
        private double speedKPH;
        private double heading;
        private double altitude;
        private double odoM;
        private boolean stopped;
        private String gpio;
        private String address;
        public Point(String data){
            if (StringTools.isBlank(data)){ return; }
            String[] _data = data.split("\\|", -1);
            this.setId(_data[0]);
            this.setVin(_data[1]);
            this.setDeviceDescription(_data[2]);
            this.setEpoch(Long.parseLong(_data[3]));
            this.setStatusCodeDesc(_data[7]);
            this.setLatitude(Double.parseDouble(_data[9]));
            this.setLongitude(Double.parseDouble(_data[10]));
            this.setGpsAge(Long.parseLong(_data[11]));
            this.setCreationAge(Long.parseLong(_data[12]));
            this.setAccuracy(Double.parseDouble(_data[13]));
            this.setNumSat(Integer.parseInt(_data[14]));
            this.setSpeedKPH(Double.parseDouble(_data[15]));
            this.setHeading(Double.parseDouble(_data[16]));
            this.setAltitude(Double.parseDouble(_data[17]));
            this.setOdoM(Double.parseDouble(_data[18]));
            this.setStopped(Boolean.parseBoolean(_data[19]));
            this.setGpio(_data[20]);
            this.setAddress(_data[21]);
        }
        public String toString(){
            String resp = this.getId()+ SEPARATE_CHAR;
            resp+=this.getVin()+SEPARATE_CHAR;
            resp+=this.getDeviceDescription()+SEPARATE_CHAR;
            resp+=this.getEpoch()+SEPARATE_CHAR;
            resp+=this.getStatusCodeDesc()+SEPARATE_CHAR;
            resp+=this.getLatitude()+SEPARATE_CHAR;
            resp+=this.getLongitude()+SEPARATE_CHAR;
            resp+=this.getGpsAge()+SEPARATE_CHAR;
            resp+=this.getCreationAge()+SEPARATE_CHAR;
            resp+=this.getAccuracy()+SEPARATE_CHAR;
            resp+=this.getNumSat()+SEPARATE_CHAR;
            resp+=this.getSpeedKPH()+SEPARATE_CHAR;
            resp+=this.getHeading()+SEPARATE_CHAR;
            resp+=this.getAltitude()+SEPARATE_CHAR;
            resp+=this.getOdoM()+SEPARATE_CHAR;
            resp+=this.isStopped()+SEPARATE_CHAR;
            resp+=this.getGpio()+SEPARATE_CHAR;
            resp+=this.getAddress();
            return resp;
        }
        public JSONObject toJSONObject(){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MD_ID,           this.getId());
                jsonObject.put(MD_VIN,          this.getVin());
                jsonObject.put(MD_DESC,         this.getDeviceDescription());
                jsonObject.put(MD_EPOCH,        this.getEpoch());
                jsonObject.put(MD_STATUS,       this.getStatusCodeDesc());
                jsonObject.put(MD_LATITUDE,     this.getLatitude());
                jsonObject.put(MD_LONGITUDE,    this.getLongitude());
                jsonObject.put(MD_GPS_AGE,      this.getGpsAge());
                jsonObject.put(MD_CREATION_AGE, this.getCreationAge());
                jsonObject.put(MD_ACCURACY,     this.getAccuracy());
                jsonObject.put(MD_SAT_COUNT,    this.getNumSat());
                jsonObject.put(MD_SPEED,        this.getSpeedKPH());
                jsonObject.put(MD_HEADING,      this.getHeading());
                jsonObject.put(MD_ALTITUDE,     this.getAltitude());
                jsonObject.put(MD_ODOM,         this.getOdoM());
                jsonObject.put(MD_IS_STOPPED,   this.isStopped());
                jsonObject.put(MD_GP_IO,        this.getGpio());
                jsonObject.put(MD_ADDRESS,      this.getAddress());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return jsonObject;
        }
        public String   getId() { return id; }
        public void     setId(String id) { this.id = id; }
        public String   getVin() { return vin; }
        public void     setVin(String vin) { this.vin = vin; }
        public String   getDeviceDescription() { return deviceDescription; }
        public void     setDeviceDescription(String deviceDescription) { this.deviceDescription = deviceDescription; }
        public long     getEpoch() { return epoch; }
        public void     setEpoch(long epoch) { this.epoch = epoch; }
        public String   getStatusCodeDesc() { return statusCodeDesc; }
        public void     setStatusCodeDesc(String statusCodeDesc) { this.statusCodeDesc = statusCodeDesc; }
        public double   getLatitude() { return latitude; }
        public void     setLatitude(double latitude) { this.latitude = latitude; }
        public double   getLongitude() { return longitude; }
        public void     setLongitude(double longitude) { this.longitude = longitude; }
        public long     getGpsAge() { return gpsAge; }
        public void     setGpsAge(long gpsAge) { this.gpsAge = gpsAge; }
        public long     getCreationAge() { return creationAge; }
        public void     setCreationAge(long creationAge) { this.creationAge = creationAge; }
        public double   getAccuracy() { return accuracy; }
        public void     setAccuracy(double accuracy) { this.accuracy = accuracy; }
        public int      getNumSat() { return numSat; }
        public void     setNumSat(int numSat) { this.numSat = numSat; }
        public double   getSpeedKPH() { return speedKPH; }
        public void     setSpeedKPH(double speedKPH) { this.speedKPH = speedKPH; }
        public double   getHeading() { return heading; }
        public void     setHeading(double heading) { this.heading = heading; }
        public double   getAltitude() { return altitude; }
        public void     setAltitude(double altitude) { this.altitude = altitude; }
        public double   getOdoM() { return odoM; }
        public void     setOdoM(double odoM) { this.odoM = odoM; }
        public boolean  isStopped() { return stopped; }
        public void     setStopped(boolean stopped) { this.stopped = stopped; }
        public String   getGpio() { return gpio; }
        public void     setGpio(String gpio) { this.gpio = gpio; }
        public String   getAddress() { return address; }
        public void     setAddress(String address) { this.address = address; }
    }
    public MapData(String data){
        if (StringTools.isBlank(data)){
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if(jsonObject == null){
            return;
        }
        new MapData(jsonObject);
    }
    private static final String MAPPING_DATA_SETS       = "DataSets";
    private static final String MAPPING_DATA_POINTS     = "Points";
    public static final String SEPARATE_CHAR            = ",";
    private Point[] points;
    public Point[] getPoints() {
        return points;
    }
    public void setPoints(Point[] points) {
        this.points = points;
    }
    public MapData(JSONObject jsonData){
        try {
            List<Point> pts = new ArrayList<>();
            JSONArray dataSets = jsonData.getJSONArray(MAPPING_DATA_SETS);
            for (int j=0; j < dataSets.length(); j++){
                JSONArray _pts = dataSets.getJSONObject(j).getJSONArray(MAPPING_DATA_POINTS);
                for(int i=0; i<_pts.length(); i++){
                    Point pt = new Point(_pts.getString(i));
                    pts.add(pt);
                }
            }
            this.setPoints(pts.toArray(new Point[pts.size()]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

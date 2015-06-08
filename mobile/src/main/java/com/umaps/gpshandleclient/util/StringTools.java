package com.umaps.gpshandleclient.util;

import com.umaps.gpshandleclient.StringConstants;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by vu@umaps.vn on 27/01/2015.
 */
public class StringTools implements StringConstants {
    public static boolean isBlank(String s){
        return s == null || s.length() == 0;
    }

    public static JSONObject createAuthenticationObject(String accountID, String userID, String password) throws JSONException {
        JSONObject jsonAuthentication = new JSONObject();
        jsonAuthentication.put(KEY_ACCOUNT, accountID);
        jsonAuthentication.put(KEY_USER, userID);
        jsonAuthentication.put(KEY_PASSWORD, password);
        return jsonAuthentication;
    }
    public static JSONObject createRequest(String accountID, String userID, String password,
                                           String command, String locale,
                                           JSONObject jsonParamsObject)
            throws JSONException
    {
        JSONObject jsonRequestObject = new JSONObject();
        jsonRequestObject.put(KEY_COMMAND, command);
        jsonRequestObject.put(KEY_LOCALE, locale);
        jsonRequestObject.put(KEY_PARAMS, jsonParamsObject);

        JSONObject request = new JSONObject();
        request.put(KEY_REQUEST, jsonRequestObject);
        request.put(KEY_AUTHENTICATION, createAuthenticationObject(accountID, userID, password));
        return request;
    }

    /**
     * Private functions for preparing data for listView*/
//    public static ArrayList<Group> createGroupList(JSONObject dataGroup) throws JSONException {
//        if (dataGroup==null) return null;
//        ArrayList<Group> groupsList = new ArrayList<>();
//        JSONArray dataGroupArray = dataGroup.getJSONArray(KEY_RESULTS);
//        for (int i = 0; i<dataGroupArray.length(); i++) {
//            JSONObject jsonGroup = dataGroupArray.getJSONObject(i);
//            String accountID        = jsonGroup.getString("accountID");
//            String groupID          = jsonGroup.getString("groupID");
//            String pushpinID        = jsonGroup.getString("pushpinID");
//            String groupDescription = jsonGroup.getString("description");
//            String groupDisplay     = jsonGroup.getString("displayName");
//            int deviceCount         = jsonGroup.getInt("deviceCount");
//
//            //--for check if device is online
//            long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
//            int countLive = 0;
//            JSONArray deviceList = null;
//            if (deviceCount>0) {
//                deviceList = jsonGroup.getJSONArray("devicesList");
//            }
//            if (deviceList == null){
//                continue;
//            }
//            for (int j = 0; j<deviceList.length(); j++){
//                JSONObject jsonDevice   = deviceList.getJSONObject(j);
//                boolean isActive        = jsonDevice.getBoolean("isActive");
//                long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");
//                if(isActive && (currentTimestamp - lastEventTimestamp < 300)){
//                    countLive++;
//                }
//            }
//            Group group = new Group(accountID, groupID, (groupDisplay==null?groupDescription:groupDisplay),
//                    (groupDisplay==null?groupDescription:groupDisplay),
//                    pushpinID/*icon*/, countLive/*live*/, deviceCount);
//            groupsList.add(group);
//        }
//        return groupsList;
//    }
    /*public static HashMap<String, ArrayList<Device>> createDevicesInGroup(JSONObject dataGroup) throws JSONException {
        if (dataGroup==null) return null;
        HashMap<String, ArrayList<Device>> hashMapGroupDevices = new HashMap<>();
        JSONArray dataGroupArray = dataGroup.getJSONArray(KEY_RESULTS);
        for (int i = 0; i<dataGroupArray.length(); i++) {
            //--Reset device-list
            ArrayList<Device> devicesList = new ArrayList<>();
            JSONObject jsonGroup = dataGroupArray.getJSONObject(i);
            String groupID          = jsonGroup.getString("groupID");
            JSONArray jsonArrayDevices = jsonGroup.getJSONArray("devicesList");
            //--for check if device is online
            long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
            for (int j = 0; j<jsonArrayDevices.length(); j++){
                JSONObject jsonDevice = jsonArrayDevices.getJSONObject(j);
                boolean isActive        = jsonDevice.getBoolean("isActive");
                String icon             = jsonDevice.getString("pushpinID");
                String deviceID         = jsonDevice.getString("deviceID");
                String description      = jsonDevice.getString("description");
                Double lastBatteryLevel = jsonDevice.getDouble("lastBatteryLevel");
                long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");
                boolean isLive          = (isActive && (currentTimestamp-lastEventTimestamp<300));
                //--Create new Device Object Model
                Device device = new Device(deviceID, description, icon, isLive, lastEventTimestamp);
                device.setBatteryLevel(lastBatteryLevel);
                devicesList.add(device);
            }
            hashMapGroupDevices.put(groupID, devicesList);
        }
        return hashMapGroupDevices;
    }*/
}

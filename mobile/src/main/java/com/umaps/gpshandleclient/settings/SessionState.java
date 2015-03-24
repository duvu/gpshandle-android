package com.umaps.gpshandleclient.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class SessionState /*extends Application*/ {
    private static final String TAG = "SessionState";
    private static final String DEVICE_GROUP_SHARED = "deviceGroupShared";
    private static int aclAdminAccount;
    private static int aclAdminDevice;
    private static int aclAdminDriver;
    private static int aclAdminGeozone;
    private static int aclAdminGroup;
    private static int aclAdminRole;
    private static int aclAdminRule;
    private static int aclAdminUser;
    private static int aclAdminUserManager;
    private static int aclMapHistory;
    private static int aclMapMonitor;
    private static int aclReportSummary;
    private static int aclReportParking;
    private static int aclReportGeozone;
    private static int aclReportDetail;


    public static int getAclAdminAccount() {
        return aclAdminAccount;
    }

    public static void setAclAdminAccount(int aclAdminAccount) {
        SessionState.aclAdminAccount = aclAdminAccount;
    }

    public static int getAclAdminDevice() {
        return aclAdminDevice;
    }

    public static void setAclAdminDevice(int aclAdminDevice) {
        SessionState.aclAdminDevice = aclAdminDevice;
    }

    public static int getAclAdminDriver() {
        return aclAdminDriver;
    }

    public static void setAclAdminDriver(int aclAdminDriver) {
        SessionState.aclAdminDriver = aclAdminDriver;
    }

    public static int getAclAdminGeozone() {
        return aclAdminGeozone;
    }

    public static void setAclAdminGeozone(int aclAdminGeozone) {
        SessionState.aclAdminGeozone = aclAdminGeozone;
    }

    public static int getAclAdminGroup() {
        return aclAdminGroup;
    }

    public static void setAclAdminGroup(int aclAdminGroup) {
        SessionState.aclAdminGroup = aclAdminGroup;
    }

    public static int getAclAdminRole() {
        return aclAdminRole;
    }

    public static void setAclAdminRole(int aclAdminRole) {
        SessionState.aclAdminRole = aclAdminRole;
    }

    public static int getAclAdminRule() {
        return aclAdminRule;
    }

    public static void setAclAdminRule(int aclAdminRule) {
        SessionState.aclAdminRule = aclAdminRule;
    }

    public static int getAclAdminUser() {
        return aclAdminUser;
    }

    public static void setAclAdminUser(int aclAdminUser) {
        SessionState.aclAdminUser = aclAdminUser;
    }

    public static int getAclAdminUserManager() {
        return aclAdminUserManager;
    }

    public static void setAclAdminUserManager(int aclAdminUserManager) {
        SessionState.aclAdminUserManager = aclAdminUserManager;
    }

    public static int getAclMapHistory() {
        return aclMapHistory;
    }

    public static void setAclMapHistory(int aclMapHistory) {
        SessionState.aclMapHistory = aclMapHistory;
    }

    public static int getAclMapMonitor() {
        return aclMapMonitor;
    }

    public static void setAclMapMonitor(int aclMapMonitor) {
        SessionState.aclMapMonitor = aclMapMonitor;
    }

    public static int getAclReportSummary() {
        return aclReportSummary;
    }

    public static void setAclReportSummary(int aclReportSummary) {
        SessionState.aclReportSummary = aclReportSummary;
    }

    public static int getAclReportParking() {
        return aclReportParking;
    }

    public static void setAclReportParking(int aclReportParking) {
        SessionState.aclReportParking = aclReportParking;
    }

    public static int getAclReportGeozone() {
        return aclReportGeozone;
    }

    public static void setAclReportGeozone(int aclReportGeozone) {
        SessionState.aclReportGeozone = aclReportGeozone;
    }

    public static int getAclReportDetail() {
        return aclReportDetail;
    }

    public static void setAclReportDetail(int aclReportDetail) {
        SessionState.aclReportDetail = aclReportDetail;
    }

    private static ProgressDialog progressDialog;
    private static boolean isFleet = true;
    private static String subTitle;

    private static String accountID;
    private static String userID;
    private static String password;

    private static String locale = "en";
    private static String token; //--

    private static String selDevice;
    private static String selGroup;
    private static String selGroupDesc;

    private static long timeFrom = 0;
    private static long timeTo = 0;

    public static void setTimeRange(long f, long t){
        timeFrom = f;
        timeTo = t;
    }
    public static long getTimeFrom(){
        return timeFrom;
    }
    public static long getTimeTo(){
        return timeTo;
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public static void setProgressDialog(ProgressDialog progressDialog) {
        SessionState.progressDialog = progressDialog;
    }

    public static boolean isIsFleet() {
        return isFleet;
    }

    public static void setIsFleet(boolean isFleet) {
        SessionState.isFleet = isFleet;
    }
    public static void setSubTitle(String subTitle){
        SessionState.subTitle = subTitle;
    }
    public static String getSubTitle(){
        return SessionState.subTitle;
    }

    public static String getSelDevice() {
        return selDevice;
    }

    public static void setSelDevice(String selDevice) {
        SessionState.selDevice = selDevice;
    }

    public static String getSelGroup() {
        return selGroup;
    }

    public static void setSelGroup(String selGroup) {
        SessionState.selGroup = selGroup;
    }

    public static String getSelGroupDesc(){
        return selGroupDesc;
    }
    public static void setSelGroupDesc(String selGroupDesc1){
        selGroupDesc = selGroupDesc1;
    }

    public static String getAccountID() {
        return accountID;
    }

    public static void setAccountID(String accountID) {
        SessionState.accountID = accountID;
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        SessionState.userID = userID;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        SessionState.password = password;
    }

    public static String getLocale() {
        return locale;
    }

    public static void setLocale(String locale) {
        SessionState.locale = locale;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        SessionState.token = token;
    }

    public static void setGroupList(Context context, JSONObject data){
        //--save to preferenceShare
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (data == null){return;}
        editor.putString(DEVICE_GROUP_SHARED, data.toString());
        editor.commit();
    }
    public static JSONObject getGroupList(Context context) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String dgString = prefs.getString(DEVICE_GROUP_SHARED, null);
        return new JSONObject(dgString);
    }
//
//
//    //--------------------------------------------------------------------------------------------//
//    private static ArrayList<Group> groupList = null;
//    private static HashMap<String, ArrayList<Device>> hashMapGroupDevices = null;
//    public static void setGroupList(JSONObject data){
//        long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
//        if (groupList == null){
//            groupList = new ArrayList<>();
//        }
//        if (hashMapGroupDevices == null){
//            hashMapGroupDevices = new HashMap<>();
//        }
//        try {
//            JSONArray groupJsonArray = null;
//            groupJsonArray = data.getJSONArray(KEY_RESULTS);
//            if (groupJsonArray==null) return; //-- ignore if null
//            for (int i = 0; i < groupJsonArray.length(); i++){
//                JSONObject itemObject = groupJsonArray.getJSONObject(i);
//                if (itemObject==null) return;
//                //String accountID, String groupId, String description, String displayName, String icon, int live, int deviceCount
//                String acctID = itemObject.getString("accountID");
//                String groupID = itemObject.getString("groupID");
//                String description = itemObject.getString("description");
//                String displayName = itemObject.getString("displayName");
//                String icon = itemObject.getString("pushpinID");
//                Group itemGroup = new Group(acctID, groupID, description, displayName, icon, 0, 0);
//                //-- process device
//                JSONArray deviceJsonArray = itemObject.getJSONArray("devicesList");
//                if (deviceJsonArray!=null) {
//                    itemGroup.setDeviceCount(deviceJsonArray.length());
//                    ArrayList<Device> listDeviceForGroup = new ArrayList<>();
//                    for (int ii = 0; ii < deviceJsonArray.length(); i++){
//                        JSONObject itemDevice = deviceJsonArray.getJSONObject(ii);
//                        if (itemDevice!=null){
//                            String deviceID = itemDevice.getString("deviceID");
//                            String deviceDesc = itemDevice.getString("description");
//                            String deviceDisp = itemDevice.getString("displayName");
//                            boolean isActive = itemDevice.getBoolean("isActive");
//                            String devicePP = itemDevice.getString("pushpinID");
//                            long llTimestamp = itemDevice.getLong("lastEventTimestamp");
//                            //String deviceID, String description, String icon, boolean isLive, long lastEventTime
//                            boolean isLive = (isActive && (currentTimestamp-llTimestamp<300));
//                            Device device = new Device(deviceID, description, devicePP, isLive, llTimestamp);
//                            device.setDescription(deviceDesc);
//                            device.setDisplayName(deviceDisp);
//                            listDeviceForGroup.add(device);
//                        }
//                    }
//                    hashMapGroupDevices.put(groupID, listDeviceForGroup);
//                }
//                groupList.add(itemGroup);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "Error when parsing response-data");
//            return;
//        }
//    }
//
//    public static ArrayList<Group> getGroupArrayList(){
//        return groupList;
//    }
//    public  static HashMap<String, ArrayList<Device>> getDeviceHashMap(){
//        return hashMapGroupDevices;
//    }
}

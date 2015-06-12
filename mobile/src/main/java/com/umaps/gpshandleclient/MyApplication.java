package com.umaps.gpshandleclient;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MyApplication extends Application {
    private static MyApplication instance = null;
    private static Typeface mIconFont = null;
    private static Typeface mTextFont = null;

    private static final String GPS_HANDLE_CLIENT       = "GPS_HANDLE_CLIENT";
    public static final String ACL_ADMIN_ACCOUNT        = "acl.service.admin.account";
    public static final String ACL_ADMIN_DEVICE         = "acl.service.admin.device";
    public static final String ACL_ADMIN_DRIVER         = "acl.service.admin.driver";
    public static final String ACL_ADMIN_GEOZONE        = "acl.service.admin.geozone";
    public static final String ACL_ADMIN_GROUP          = "acl.service.admin.group";
    public static final String ACL_ADMIN_ROLE           = "acl.service.admin.role";
    public static final String ACL_ADMIN_RULE           = "acl.service.admin.rule";
    public static final String ACL_ADMIN_USER           = "acl.service.admin.user";
    public static final String ACL_ADMIN_USER_MANAGER   = "acl.service.admin.user.manager";
    public static final String ACL_MAP_HISTORY          = "acl.service.map.history";
    public static final String ACL_MAP_MONITOR          = "acl.service.map.monitor";
    public static final String ACL_REPORT_DETAIL        = "acl.service.report.detail";
    public static final String ACL_REPORT_GEOZONE       = "acl.service.report.geozone";
    public static final String ACL_REPORT_PARKING       = "acl.service.report.parking";
    public static final String ACL_REPORT_SUMMARY       = "acl.service.report.summary";

    private int aclAdminAccount;
    private int aclAdminDevice;
    private int aclAdminDriver;
    private int aclAdminGeozone;
    private int aclAdminGroup;
    private int aclAdminRole;
    private int aclAdminRule;
    private int aclAdminUser;
    private int aclAdminUserManager;
    private int aclMapHistory;
    private int aclMapMonitor;
    private int aclReportSummary;
    private int aclReportParking;
    private int aclReportGeozone;
    private int aclReportDetail;

    public static MyApplication getInstance(){
        return instance;
    }
    public static Typeface getIconFont(){
        return mIconFont;
    }
    public static Typeface getTextFont(){
        return mTextFont;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        instance = this;
        mIconFont = Typeface.createFromAsset(this.getAssets(), "icomoon.ttf");
        mTextFont = Typeface.createFromAsset(this.getAssets(), "OpenSans-Regular.ttf");
        populateSettings();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        storeSettings();
    }
    public long timeInterval    = 20000;
    private boolean isFleet     = true;

    private String accountID;
    private String userID;
    private String password;

    private String locale = "en";
    private String token; //--
    private long expireOn;

    private String selDevice;
    private String selGroup;
    private String selGroupDesc;
    private String selDeviceDesc;

    private String groupList;

    private boolean isSignedIn;

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public boolean isFleet() {
        return isFleet;
    }

    public void setIsFleet(boolean isFleet) {
        this.isFleet = isFleet;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(long expireOn) {
        this.expireOn = expireOn;
    }

    public String getSelDevice() {
        return selDevice;
    }

    public void setSelDevice(String selDevice) {
        this.selDevice = selDevice;
    }

    public String getSelGroup() {
        return (StringTools.isBlank(selGroup)?"all":selGroup);
    }

    public void setSelGroup(String selGroup) {
        this.selGroup = selGroup;
    }

    public String getSelGroupDesc() {
        return (StringTools.isBlank(selGroup)||selGroup.equalsIgnoreCase("all"))? "All":selGroupDesc;
    }

    public void setSelGroupDesc(String selGroupDesc) {
        this.selGroupDesc = selGroupDesc;
    }

    public String getSelDeviceDesc() {
        return selDeviceDesc;
    }

    public void setSelDeviceDesc(String selDeviceDesc) {
        this.selDeviceDesc = selDeviceDesc;
    }

    public String getGroupList() {
        return groupList;
    }

    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }

    public boolean isSignedIn() {
        return isSignedIn;
    }

    public void setIsSignedIn(boolean isSignedIn) {
        this.isSignedIn = isSignedIn;
    }

    private static final String ACCOUNT_ID      = "accountID";
    private static final String USER_ID         = "userID";
    private static final String PASSWORD        = "password";
    private static final String TOKEN           = "token";
    private static final String EXPIRED_ON      = "expiredOn";
    private static final String LOCALE          = "locale";
    private static final String SEL_GROUP_DESC  = "selGroupDesc";
    private static final String SEL_GROUP       = "selGroup";
    private static final String SEL_DEVICE      = "selDevice";
    private static final String SEL_DEVICE_DESC = "selDeviceDesc";
    private static final String IS_FLEET        = "isFleet";
    private static final String LOADED_GROUP    = "loadedGroupData";
    private static final String LOADED_DEVICE   = "loadedDeviceData";
    private static final String IS_SIGNED_IN    = "isSignedIn";

    public void populateSettings(){
        //SharedPreferences prefs = this.getSharedPreferences(GPS_HANDLE_CLIENT, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.setAccountID(prefs.getString(ACCOUNT_ID, ""));
        this.setUserID(prefs.getString(USER_ID, ""));
        this.setPassword(prefs.getString(PASSWORD, ""));
        this.setToken(prefs.getString(TOKEN, ""));
        this.setExpireOn(prefs.getLong(EXPIRED_ON, 0));
        this.setLocale(prefs.getString(LOCALE, "en"));
        this.setSelDevice(prefs.getString(SEL_DEVICE, ""));
        this.setSelDeviceDesc(prefs.getString(SEL_DEVICE_DESC, ""));
        this.setSelGroup(prefs.getString(SEL_GROUP, ""));
        this.setSelGroupDesc(prefs.getString(SEL_GROUP_DESC, ""));
        this.setIsFleet(prefs.getBoolean(IS_FLEET, true));
        this.setGroupList(prefs.getString(LOADED_GROUP, ""));
        this.setIsSignedIn(prefs.getBoolean(IS_SIGNED_IN, false));
    }

    public void storeSettings(){
        //SharedPreferences prefs = this.getSharedPreferences(GPS_HANDLE_CLIENT, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCOUNT_ID, this.getAccountID());
        editor.putString(USER_ID, this.getUserID());
        editor.putString(PASSWORD, this.getPassword());
        editor.putString(TOKEN, this.getToken());
        editor.putLong(EXPIRED_ON, this.getExpireOn());
        editor.putString(LOCALE, this.getLocale());
        editor.putString(SEL_DEVICE, this.getSelDevice());
        editor.putString(SEL_DEVICE_DESC, this.getSelDeviceDesc());
        editor.putString(SEL_GROUP, this.getSelGroup());
        editor.putString(SEL_GROUP_DESC, this.getSelGroupDesc());
        editor.putBoolean(IS_FLEET, this.isFleet());
        editor.putString(LOADED_GROUP, this.getGroupList());
        editor.putBoolean(IS_SIGNED_IN, this.isSignedIn());
        editor.commit();
    }

    public void cleanSettings() {
        this.setAccountID(null);
        this.setUserID(null);
        this.setPassword(null);
        this.setToken(null);
        this.setLocale(null);
        this.setSelDevice(null);
        this.setSelGroup(null);
        this.setIsFleet(true);
        this.setGroupList(null);
        this.setIsSignedIn(false);
    }

    public int getAclAdminAccount() {
        return aclAdminAccount;
    }

    public void setAclAdminAccount(int aclAdminAccount) {
        this.aclAdminAccount = aclAdminAccount;
    }

    public int getAclAdminDevice() {
        return aclAdminDevice;
    }

    public void setAclAdminDevice(int aclAdminDevice) {
        this.aclAdminDevice = aclAdminDevice;
    }

    public int getAclAdminDriver() {
        return aclAdminDriver;
    }

    public void setAclAdminDriver(int aclAdminDriver) {
        this.aclAdminDriver = aclAdminDriver;
    }

    public int getAclAdminGeozone() {
        return aclAdminGeozone;
    }

    public void setAclAdminGeozone(int aclAdminGeozone) {
        this.aclAdminGeozone = aclAdminGeozone;
    }

    public int getAclAdminGroup() {
        return aclAdminGroup;
    }

    public void setAclAdminGroup(int aclAdminGroup) {
        this.aclAdminGroup = aclAdminGroup;
    }

    public int getAclAdminRole() {
        return aclAdminRole;
    }

    public void setAclAdminRole(int aclAdminRole) {
        this.aclAdminRole = aclAdminRole;
    }

    public int getAclAdminRule() {
        return aclAdminRule;
    }

    public void setAclAdminRule(int aclAdminRule) {
        this.aclAdminRule = aclAdminRule;
    }

    public int getAclAdminUser() {
        return aclAdminUser;
    }

    public void setAclAdminUser(int aclAdminUser) {
        this.aclAdminUser = aclAdminUser;
    }

    public int getAclAdminUserManager() {
        return aclAdminUserManager;
    }

    public void setAclAdminUserManager(int aclAdminUserManager) {
        this.aclAdminUserManager = aclAdminUserManager;
    }

    public int getAclMapHistory() {
        return aclMapHistory;
    }

    public void setAclMapHistory(int aclMapHistory) {
        this.aclMapHistory = aclMapHistory;
    }

    public int getAclMapMonitor() {
        return aclMapMonitor;
    }

    public void setAclMapMonitor(int aclMapMonitor) {
        this.aclMapMonitor = aclMapMonitor;
    }

    public int getAclReportSummary() {
        return aclReportSummary;
    }

    public void setAclReportSummary(int aclReportSummary) {
        this.aclReportSummary = aclReportSummary;
    }

    public int getAclReportParking() {
        return aclReportParking;
    }

    public void setAclReportParking(int aclReportParking) {
        this.aclReportParking = aclReportParking;
    }

    public int getAclReportGeozone() {
        return aclReportGeozone;
    }

    public void setAclReportGeozone(int aclReportGeozone) {
        this.aclReportGeozone = aclReportGeozone;
    }

    public int getAclReportDetail() {
        return aclReportDetail;
    }

    public void setAclReportDetail(int aclReportDetail) {
        this.aclReportDetail = aclReportDetail;
    }

    public void storeAcls(JSONObject acl) {
        String name = null;
        try {
            name = acl.getString("name");
            int value = acl.getInt("value");
            String description = acl.getString("description");
            if (name.equalsIgnoreCase(ACL_ADMIN_ACCOUNT))           { setAclAdminAccount(value); }
            else if (name.equalsIgnoreCase(ACL_ADMIN_DEVICE))       { setAclAdminDevice(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_DRIVER))       { setAclAdminDriver(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_GEOZONE))      { setAclAdminGeozone(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_GROUP))        { setAclAdminGroup(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_ROLE))         { setAclAdminRole(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_RULE))         { setAclAdminRule(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_USER))         { setAclAdminUser(value);}
            else if (name.equalsIgnoreCase(ACL_ADMIN_USER_MANAGER)) { setAclAdminUserManager(value);}
            else if (name.equalsIgnoreCase(ACL_MAP_HISTORY))        { setAclMapHistory(value);}
            else if (name.equalsIgnoreCase(ACL_MAP_MONITOR))        { setAclMapMonitor(value);}
            else if (name.equalsIgnoreCase(ACL_REPORT_DETAIL))      { setAclReportDetail(value);}
            else if (name.equalsIgnoreCase(ACL_REPORT_GEOZONE))     { setAclReportGeozone(value);}
            else if (name.equalsIgnoreCase(ACL_REPORT_PARKING))     { setAclReportParking(value);}
            else if (name.equalsIgnoreCase(ACL_REPORT_SUMMARY))     { setAclReportSummary(value);}
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentAdmin() {
        return "admin".equalsIgnoreCase(this.userID);
    }
}

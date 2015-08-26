package com.umaps.gpshandleclient;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.umaps.gpshandleclient.model.ParseDevice;
import com.umaps.gpshandleclient.model.ParseGroup;
import com.umaps.gpshandleclient.model.ParseLoginEvent;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication instance = null;
    private static Typeface mIconFont = null;
    private static Typeface mTextFont = null;

    private static final String ACCOUNT_ID              = "accountID";
    private static final String USER_ID                 = "userID";
    private static final String PASSWORD                = "password";
    private static final String TOKEN                   = "token";
    private static final String EXPIRED_ON              = "expiredOn";

    private static final String USER_DISPLAY_NAME       = "displayName";
    private static final String USER_DESCRIPTION        = "desciption";
    private static final String CONTACT_NAME            = "contactName";
    private static final String CONTACT_EMAIL           = "contactEmail";
    private static final String CONTACT_PHONE           = "contactPhone";
    private static final String CREATION_TIME           = "creationTime";
    private static final String LAST_LOGIN_TIME         = "lastLoginTime";

    private static final String TOTAL_DEVICES           = "totalDevices";
    private static final String SELECTED_GROUP          = "selGroup";
    private static final String GROUP_POSITION          = "groupPosition";

    private static final String LOCALE                  = "locale";
    private static final String SEL_GROUP_DESC          = "selGroupDesc";
    private static final String SEL_DEVICE              = "selDevice";
    private static final String SEL_DEVICE_DESC         = "selDeviceDesc";
    private static final String IS_FLEET                = "isFleet";
    private static final String LOADED_GROUP            = "loadedGroupData";
    private static final String LOADED_DEVICE           = "loadedDeviceData";
    private static final String IS_SIGNED_IN            = "isSignedIn";

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
        super.onCreate();

        //-- register class
        ParseObject.registerSubclass(ParseLoginEvent.class);
        ParseObject.registerSubclass(ParseGroup.class);
        ParseObject.registerSubclass(ParseDevice.class);
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        ParseUser.enableAutomaticUser();
        Parse.initialize(this);

        ParsePush.subscribeInBackground("GPS");

        ParseInstallation.getCurrentInstallation().saveInBackground();

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
        super.onTerminate();
        storeSettings();
    }

    public long timeInterval    = 20000;
    private boolean isFleet     = true;
    private String locale = "en";
    private String selDevice;
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSelDevice() {
        return selDevice;
    }

    public void setSelDevice(String selDevice) {
        this.selDevice = selDevice;
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

    public void populateSettings(){
        //SharedPreferences prefs = this.getSharedPreferences(GPS_HANDLE_CLIENT, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Session.setAccountId(prefs.getString(ACCOUNT_ID, ""));
        Session.setUserId(prefs.getString(USER_ID, ""));
        Session.setSessionToken(prefs.getString(TOKEN, ""));
        Session.setTokenExpired(prefs.getLong(EXPIRED_ON, 0L));
        Session.setUserPassword(prefs.getString(PASSWORD, ""));
        Session.setDisplayName(prefs.getString(USER_DISPLAY_NAME, ""));
        Session.setDescription(prefs.getString(USER_DESCRIPTION, ""));
        Session.setContactName(prefs.getString(CONTACT_NAME, ""));
        Session.setContactPhone(prefs.getString(CONTACT_PHONE, ""));
        Session.setContactEmail(prefs.getString(CONTACT_EMAIL, ""));
        Session.setCreationTime(prefs.getLong(CREATION_TIME, 0L));
        Session.setLastLoginTime(prefs.getLong(LAST_LOGIN_TIME, 0L));
        Session.setTotalDevices(prefs.getInt(TOTAL_DEVICES, 0));
        Session.setSelectedGroup(prefs.getString(SELECTED_GROUP, "all"));
        Session.setGroupPosition(prefs.getInt(GROUP_POSITION, 0));

        this.setLocale(prefs.getString(LOCALE, "en"));
        this.setSelDevice(prefs.getString(SEL_DEVICE, ""));
        this.setSelDeviceDesc(prefs.getString(SEL_DEVICE_DESC, ""));
        this.setIsFleet(prefs.getBoolean(IS_FLEET, true));
        this.setGroupList(prefs.getString(LOADED_GROUP, ""));
        this.setIsSignedIn(prefs.getBoolean(IS_SIGNED_IN, false));

        //-- Acl
        this.setAclAdminAccount(prefs.getInt(ACL_ADMIN_ACCOUNT, 0));
        this.setAclAdminDevice(prefs.getInt(ACL_ADMIN_DEVICE, 0));
        this.setAclAdminDriver(prefs.getInt(ACL_ADMIN_DRIVER, 0));
        this.setAclAdminGeozone(prefs.getInt(ACL_ADMIN_GEOZONE, 0));
        this.setAclAdminGroup(prefs.getInt(ACL_ADMIN_GROUP, 0));
        this.setAclAdminRole(prefs.getInt(ACL_ADMIN_ROLE, 0));
        this.setAclAdminRule(prefs.getInt(ACL_ADMIN_RULE, 0));
        this.setAclAdminUser(prefs.getInt(ACL_ADMIN_USER, 0));
        this.setAclAdminUserManager(prefs.getInt(ACL_ADMIN_USER_MANAGER, 0));
        this.setAclMapHistory(prefs.getInt(ACL_MAP_HISTORY, 0));
        this.setAclMapMonitor(prefs.getInt(ACL_MAP_MONITOR, 0));
        this.setAclReportDetail(prefs.getInt(ACL_REPORT_DETAIL, 0));
        this.setAclReportGeozone(prefs.getInt(ACL_REPORT_GEOZONE, 0));
        this.setAclReportParking(prefs.getInt(ACL_REPORT_PARKING, 0));
        this.setAclReportSummary(prefs.getInt(ACL_REPORT_SUMMARY, 0));
    }

    public void storeSettings(){
        //SharedPreferences prefs = this.getSharedPreferences(GPS_HANDLE_CLIENT, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCOUNT_ID, Session.getAccountId());
        editor.putString(USER_ID, Session.getUserId());
        editor.putString(PASSWORD, Session.getUserPassword());
        editor.putString(TOKEN, Session.getSessionToken());
        editor.putLong(EXPIRED_ON, Session.getTokenExpired());

        editor.putString(USER_DISPLAY_NAME, Session.getDisplayName());
        editor.putString(USER_DESCRIPTION, Session.getDescription());
        editor.putString(CONTACT_NAME, Session.getContactName());
        editor.putString(CONTACT_PHONE, Session.getContactPhone());
        editor.putString(CONTACT_EMAIL, Session.getContactEmail());
        editor.putLong(CREATION_TIME, Session.getCreationTime());
        editor.putLong(LAST_LOGIN_TIME, Session.getLastLoginTime());

        editor.putInt(TOTAL_DEVICES, Session.getTotalDevices());
        editor.putString(SELECTED_GROUP, Session.getSelectedGroup());
        editor.putInt(GROUP_POSITION, Session.getGroupPosition());

        editor.putString(LOCALE, this.getLocale());
        editor.putString(SEL_DEVICE, this.getSelDevice());
        editor.putString(SEL_DEVICE_DESC, this.getSelDeviceDesc());
        editor.putBoolean(IS_FLEET, this.isFleet());
        editor.putString(LOADED_GROUP, this.getGroupList());
        editor.putBoolean(IS_SIGNED_IN, this.isSignedIn());

        //-- Acl
        editor.putInt(ACL_ADMIN_ACCOUNT, getAclAdminAccount());
        editor.putInt(ACL_ADMIN_DEVICE, getAclAdminDevice());
        editor.putInt(ACL_ADMIN_DRIVER, getAclAdminDriver());
        editor.putInt(ACL_ADMIN_GEOZONE, getAclAdminGeozone());
        editor.putInt(ACL_ADMIN_GROUP, getAclAdminGroup());
        editor.putInt(ACL_ADMIN_ROLE, getAclAdminRole());
        editor.putInt(ACL_ADMIN_RULE, getAclAdminRule());
        editor.putInt(ACL_ADMIN_USER, getAclAdminUser());
        editor.putInt(ACL_ADMIN_USER_MANAGER, getAclAdminUserManager());
        editor.putInt(ACL_MAP_HISTORY, getAclMapHistory());
        editor.putInt(ACL_MAP_MONITOR, getAclMapMonitor());
        editor.putInt(ACL_REPORT_DETAIL, getAclReportDetail());
        editor.putInt(ACL_REPORT_GEOZONE, getAclReportGeozone());
        editor.putInt(ACL_REPORT_PARKING, getAclReportParking());
        editor.putInt(ACL_REPORT_SUMMARY, getAclReportSummary());
        editor.commit();
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
        //Log.i(TAG, acl.toString());
        try {
            String name = acl.getString("name");
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
}

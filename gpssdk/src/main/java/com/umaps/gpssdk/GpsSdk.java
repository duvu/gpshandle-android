package com.umaps.gpssdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 16/08/2015.
 */
public class GpsSdk {

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


    private static Context context;
    private static boolean hasError;

    private static String accountId;
    private static boolean isAccountManager;
    private static String userId;

    private static String displayName;
    private static String description;
    private static String contactName;
    private static String contactPhone;
    private static String contactEmail; //will be accountEmail if user=admin
    private static long creationTime;
    private static long lastLoginTime;
    private static String userProfileImage;
    private static String sessionToken;
    private static long tokenExpired;
    private static String userPassword;

    private static int totalDevices;
    private static String selectedGroup;
    private static int groupPosition;
    private static int sessionId;

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

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        GpsSdk.context = context;
    }

    public static boolean hasError() {
        return hasError;
    }

    public static void setError(boolean hasError) {
        GpsSdk.hasError = hasError;
    }

    public static String getAccountId() {
        return accountId;
    }

    public static void setAccountId(String accountId) {
        GpsSdk.accountId = accountId;
    }

    public static boolean isAccountManager() {
        return isAccountManager;
    }

    public static void setAccountManager(boolean manager) {
        GpsSdk.isAccountManager = manager;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        GpsSdk.userId = userId;
    }



    public static String getDisplayName() {
        return displayName;
    }

    public static void setDisplayName(String displayName) {
        GpsSdk.displayName = displayName;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        GpsSdk.description = description;
    }

    public static String getContactName() {
        return contactName;
    }

    public static void setContactName(String contactName) {
        GpsSdk.contactName = contactName;
    }

    public static String getContactPhone() {
        return contactPhone;
    }

    public static void setContactPhone(String contactPhone) {
        GpsSdk.contactPhone = contactPhone;
    }

    public static String getContactEmail() {
        return contactEmail;
    }

    public static void setContactEmail(String contactEmail) {
        GpsSdk.contactEmail = contactEmail;
    }

    public static long getCreationTime() {
        return creationTime;
    }

    public static void setCreationTime(long creationTime) {
        GpsSdk.creationTime = creationTime;
    }

    public static long getLastLoginTime() {
        return lastLoginTime;
    }

    public static void setLastLoginTime(long lastLoginTime) {
        GpsSdk.lastLoginTime = lastLoginTime;
    }

    public static String getUserProfileImage() {
        return userProfileImage;
    }

    public static void setUserProfileImage(String userProfileImage) {
        GpsSdk.userProfileImage = userProfileImage;
    }

    public static String getSessionToken() {
        return sessionToken;
    }

    public static void setSessionToken(String sessionToken) {
        GpsSdk.sessionToken = sessionToken;
    }

    public static long getTokenExpired() {
        return tokenExpired;
    }

    public static void setTokenExpired(long tokenExpired) {
        GpsSdk.tokenExpired = tokenExpired;
    }

    public static String getUserPassword() {
        return userPassword;
    }

    public static void setUserPassword(String userPassword) {
        GpsSdk.userPassword = userPassword;
    }


    public static void updateAccountId (String updateAcc) {
        if (StringTools.isBlank(accountId)) {
            setAccountId(updateAcc);
        } else if (!accountId.equalsIgnoreCase(updateAcc)) {
            setAccountId(updateAcc);
        }
    }

    public static void updateUserId(String updateUser) {
        if (StringTools.isBlank(userId)) {
            setUserId(updateUser);
        } else if (!userId.equalsIgnoreCase(updateUser)) {
            setUserId(updateUser);
        }
    }

    public static void updatePassword(String updatePassword) {
        if (StringTools.isBlank(userPassword)) {
            setUserPassword(updatePassword);
        } else if (!userPassword.equalsIgnoreCase(updatePassword)) {
            setUserPassword(updatePassword);
        }
    }


    public static int getTotalDevices() {
        return totalDevices;
    }

    public static void setTotalDevices(int totalDevices) {
        GpsSdk.totalDevices = totalDevices;
    }

    public static String getSelectedGroup() {
        return selectedGroup;
    }

    public static void setSelectedGroup(String selectedGroup) {
        GpsSdk.selectedGroup = selectedGroup;
    }

    public static int getGroupPosition() {
        return groupPosition < 0 ? 0 : groupPosition;
    }

    public static void setGroupPosition(int groupPosition) {
        GpsSdk.groupPosition = groupPosition;
    }

    public static int getSessionId() {
        return sessionId;
    }

    public static void setSessionId(int sessionId) {
        GpsSdk.sessionId = sessionId;
    }


    //-- Acls


    public static int getAclAdminAccount() {
        return aclAdminAccount;
    }

    public static void setAclAdminAccount(int aclAdminAccount) {
        GpsSdk.aclAdminAccount = aclAdminAccount;
    }

    public static int getAclAdminDevice() {
        return aclAdminDevice;
    }

    public static void setAclAdminDevice(int aclAdminDevice) {
        GpsSdk.aclAdminDevice = aclAdminDevice;
    }

    public static int getAclAdminDriver() {
        return aclAdminDriver;
    }

    public static void setAclAdminDriver(int aclAdminDriver) {
        GpsSdk.aclAdminDriver = aclAdminDriver;
    }

    public static int getAclAdminGeozone() {
        return aclAdminGeozone;
    }

    public static void setAclAdminGeozone(int aclAdminGeozone) {
        GpsSdk.aclAdminGeozone = aclAdminGeozone;
    }

    public static int getAclAdminGroup() {
        return aclAdminGroup;
    }

    public static void setAclAdminGroup(int aclAdminGroup) {
        GpsSdk.aclAdminGroup = aclAdminGroup;
    }

    public static int getAclAdminRole() {
        return aclAdminRole;
    }

    public static void setAclAdminRole(int aclAdminRole) {
        GpsSdk.aclAdminRole = aclAdminRole;
    }

    public static int getAclAdminRule() {
        return aclAdminRule;
    }

    public static void setAclAdminRule(int aclAdminRule) {
        GpsSdk.aclAdminRule = aclAdminRule;
    }

    public static int getAclAdminUser() {
        return aclAdminUser;
    }

    public static void setAclAdminUser(int aclAdminUser) {
        GpsSdk.aclAdminUser = aclAdminUser;
    }

    public static int getAclAdminUserManager() {
        return aclAdminUserManager;
    }

    public static void setAclAdminUserManager(int aclAdminUserManager) {
        GpsSdk.aclAdminUserManager = aclAdminUserManager;
    }

    public static int getAclMapHistory() {
        return aclMapHistory;
    }

    public static void setAclMapHistory(int aclMapHistory) {
        GpsSdk.aclMapHistory = aclMapHistory;
    }

    public static int getAclMapMonitor() {
        return aclMapMonitor;
    }

    public static void setAclMapMonitor(int aclMapMonitor) {
        GpsSdk.aclMapMonitor = aclMapMonitor;
    }

    public static int getAclReportSummary() {
        return aclReportSummary;
    }

    public static void setAclReportSummary(int aclReportSummary) {
        GpsSdk.aclReportSummary = aclReportSummary;
    }

    public static int getAclReportParking() {
        return aclReportParking;
    }

    public static void setAclReportParking(int aclReportParking) {
        GpsSdk.aclReportParking = aclReportParking;
    }

    public static int getAclReportGeozone() {
        return aclReportGeozone;
    }

    public static void setAclReportGeozone(int aclReportGeozone) {
        GpsSdk.aclReportGeozone = aclReportGeozone;
    }

    public static int getAclReportDetail() {
        return aclReportDetail;
    }

    public static void setAclReportDetail(int aclReportDetail) {
        GpsSdk.aclReportDetail = aclReportDetail;
    }

    public static void clean() {
        accountId = null;
        isAccountManager = false;
        userId = null;
        contactEmail = null;
        userProfileImage = null;
        sessionToken = null;
        tokenExpired = -1;
        userPassword = null;
    }

    public static final void initialize(Context context) {
        if (context == null) {
            //-- setError
            setError(true);
            return;
        }
        GpsSdk.context = context;
        //--
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GpsSdk.context);
        GpsSdk.setAccountId     (prefs.getString    (ACCOUNT_ID         , ""));
        GpsSdk.setUserId        (prefs.getString    (USER_ID            , ""));
        GpsSdk.setSessionToken  (prefs.getString    (TOKEN              , ""));
        GpsSdk.setTokenExpired  (prefs.getLong      (EXPIRED_ON         , 0L));
        GpsSdk.setUserPassword  (prefs.getString    (PASSWORD           , ""));
        GpsSdk.setDisplayName   (prefs.getString    (USER_DISPLAY_NAME  , ""));
        GpsSdk.setDescription   (prefs.getString    (USER_DESCRIPTION   , ""));
        GpsSdk.setContactName   (prefs.getString    (CONTACT_NAME       , ""));
        GpsSdk.setContactPhone  (prefs.getString    (CONTACT_PHONE      , ""));
        GpsSdk.setContactEmail  (prefs.getString    (CONTACT_EMAIL      , ""));
        GpsSdk.setCreationTime  (prefs.getLong      (CREATION_TIME      , 0L));
        GpsSdk.setLastLoginTime (prefs.getLong      (LAST_LOGIN_TIME    , 0L));
        GpsSdk.setTotalDevices  (prefs.getInt       (TOTAL_DEVICES      , 0));
        GpsSdk.setSelectedGroup (prefs.getString    (SELECTED_GROUP     , "all"));
        GpsSdk.setGroupPosition (prefs.getInt       (GROUP_POSITION     , 0));

        //-- Acl
        GpsSdk.setAclAdminAccount       (prefs.getInt(ACL_ADMIN_ACCOUNT     , 0));
        GpsSdk.setAclAdminDevice        (prefs.getInt(ACL_ADMIN_DEVICE      , 0));
        GpsSdk.setAclAdminDriver        (prefs.getInt(ACL_ADMIN_DRIVER      , 0));
        GpsSdk.setAclAdminGeozone       (prefs.getInt(ACL_ADMIN_GEOZONE     , 0));
        GpsSdk.setAclAdminGroup         (prefs.getInt(ACL_ADMIN_GROUP       , 0));
        GpsSdk.setAclAdminRole          (prefs.getInt(ACL_ADMIN_ROLE        , 0));
        GpsSdk.setAclAdminRule          (prefs.getInt(ACL_ADMIN_RULE        , 0));
        GpsSdk.setAclAdminUser          (prefs.getInt(ACL_ADMIN_USER        , 0));
        GpsSdk.setAclAdminUserManager   (prefs.getInt(ACL_ADMIN_USER_MANAGER, 0));
        GpsSdk.setAclMapHistory         (prefs.getInt(ACL_MAP_HISTORY       , 0));
        GpsSdk.setAclMapMonitor         (prefs.getInt(ACL_MAP_MONITOR       , 0));
        GpsSdk.setAclReportDetail       (prefs.getInt(ACL_REPORT_DETAIL     , 0));
        GpsSdk.setAclReportGeozone      (prefs.getInt(ACL_REPORT_GEOZONE    , 0));
        GpsSdk.setAclReportParking      (prefs.getInt(ACL_REPORT_PARKING    , 0));
        GpsSdk.setAclReportSummary      (prefs.getInt(ACL_REPORT_SUMMARY    , 0));
    }



    public static final void saveInstanceState(){
        if (context == null) {
            //-- SDK has not been initialized yet
            setError(true);
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GpsSdk.context);
        SharedPreferences.Editor e = prefs.edit();
        e.putString (ACCOUNT_ID,        GpsSdk.getAccountId());
        e.putString (USER_ID,           GpsSdk.getUserId());
        e.putString (PASSWORD,          GpsSdk.getUserPassword());
        e.putString (TOKEN,             GpsSdk.getSessionToken());
        e.putLong   (EXPIRED_ON,        GpsSdk.getTokenExpired());
        e.putString (USER_DISPLAY_NAME, GpsSdk.getDisplayName());
        e.putString (USER_DESCRIPTION,  GpsSdk.getDescription());
        e.putString (CONTACT_NAME,      GpsSdk.getContactName());
        e.putString (CONTACT_PHONE,     GpsSdk.getContactPhone());
        e.putString (CONTACT_EMAIL,     GpsSdk.getContactEmail());
        e.putLong   (CREATION_TIME,     GpsSdk.getCreationTime());
        e.putLong   (LAST_LOGIN_TIME,   GpsSdk.getLastLoginTime());
        e.putInt    (TOTAL_DEVICES,     GpsSdk.getTotalDevices());
        e.putString (SELECTED_GROUP,    GpsSdk.getSelectedGroup());
        e.putInt    (GROUP_POSITION,    GpsSdk.getGroupPosition());

        //-- Acl
        e.putInt(ACL_ADMIN_ACCOUNT, getAclAdminAccount());
        e.putInt(ACL_ADMIN_DEVICE, getAclAdminDevice());
        e.putInt(ACL_ADMIN_DRIVER, getAclAdminDriver());
        e.putInt(ACL_ADMIN_GEOZONE, getAclAdminGeozone());
        e.putInt(ACL_ADMIN_GROUP, getAclAdminGroup());
        e.putInt(ACL_ADMIN_ROLE, getAclAdminRole());
        e.putInt(ACL_ADMIN_RULE, getAclAdminRule());
        e.putInt(ACL_ADMIN_USER, getAclAdminUser());
        e.putInt(ACL_ADMIN_USER_MANAGER, getAclAdminUserManager());
        e.putInt(ACL_MAP_HISTORY, getAclMapHistory());
        e.putInt(ACL_MAP_MONITOR, getAclMapMonitor());
        e.putInt(ACL_REPORT_DETAIL, getAclReportDetail());
        e.putInt(ACL_REPORT_GEOZONE, getAclReportGeozone());
        e.putInt(ACL_REPORT_PARKING, getAclReportParking());
        e.putInt(ACL_REPORT_SUMMARY, getAclReportSummary());
        //-- save
        e.commit();
    }

    public static void storeAcls(JSONObject acl) {
        //Log.i(TAG, acl.toString());
        try {
            String name = acl.getString("name");
            int value = acl.getInt("value");
            String description = acl.getString("description");
            if (name.equalsIgnoreCase(ACL_ADMIN_ACCOUNT))      { setAclAdminAccount(value); }
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

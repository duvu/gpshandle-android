package com.umaps.gpssdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    }
}

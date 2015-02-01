package com.umaps.gpshandleclient.settings;

import android.app.Application;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class ApplicationSettings extends Application {
    public static final String KEY_URL          = "keyURL";
    public static final String KEY_PORT         = "keyPort";
    public static final String KEY_ACCOUNT      = "keyAccount";
    public static final String KEY_USER         = "keyUser";
    public static final String KEY_PASSWORD     = "keyPassword";
    public static final String KEY_LOCALE       = "keyLocale";

    public static final String KEY_TOKEN        = "keyToken";
    public static final String KEY_GPS_FILE     = "keyGpsFile";
    public static final String KEY_FPS_DIR      = "keyGpsDir";

    public static final String NULL_STRING      = "";

    //-- User preferences

    private static boolean isFleet = true;
    private static String selDevice;
    private static String selGroup;

    private static String serverURL; //--Include port
    private static String mappingUrl;
    private static String reportingUrl;
    private static String administrationUrl;

    private static String sAccount;
    private static String sUser;
    private static String sPassword;
    private static String locale = "en";
    private static String sToken; //--

    public static boolean isIsFleet() {
        return isFleet;
    }

    public static void setIsFleet(boolean isFleet) {
        ApplicationSettings.isFleet = isFleet;
    }

    public static String getSelDevice() {
        return selDevice;
    }

    public static void setSelDevice(String selDevice) {
        ApplicationSettings.selDevice = selDevice;
    }

    public static String getSelGroup() {
        return selGroup;
    }

    public static void setSelGroup(String selGroup) {
        ApplicationSettings.selGroup = selGroup;
    }

    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        ApplicationSettings.serverURL = serverURL;
        setMappingUrl(ApplicationSettings.serverURL+"/monitor");
        setAdministrationUrl(ApplicationSettings.serverURL+"/admin");
        setReportingUrl(ApplicationSettings.serverURL+"/report");
    }

    public static String getMappingUrl() {
        return mappingUrl;
    }

    public static void setMappingUrl(String mappingUrl) {
        ApplicationSettings.mappingUrl = mappingUrl;
    }

    public static String getReportingUrl() {
        return reportingUrl;
    }

    public static void setReportingUrl(String reportingUrl) {
        ApplicationSettings.reportingUrl = reportingUrl;
    }

    public static String getAdministrationUrl() {
        return administrationUrl;
    }

    public static void setAdministrationUrl(String administrationUrl) {
        ApplicationSettings.administrationUrl = administrationUrl;
    }

    public static String getsAccount() {
        return sAccount;
    }

    public static void setsAccount(String sAccount) {
        ApplicationSettings.sAccount = sAccount;
    }

    public static String getsUser() {
        return sUser;
    }

    public static void setsUser(String sUser) {
        ApplicationSettings.sUser = sUser;
    }

    public static String getsPassword() {
        return sPassword;
    }

    public static void setsPassword(String sPassword) {
        ApplicationSettings.sPassword = sPassword;
    }

    public static String getLocale() {
        return locale;
    }

    public static void setLocale(String locale) {
        ApplicationSettings.locale = locale;
    }

    public static String getsToken() {
        return sToken;
    }

    public static void setsToken(String sToken) {
        ApplicationSettings.sToken = sToken;
    }
}

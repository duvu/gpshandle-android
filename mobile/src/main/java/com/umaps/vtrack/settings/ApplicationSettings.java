package com.umaps.vtrack.settings;

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


    private static String serverURL; //--Include port
    private static String mappingUrl;
    private static String reportingUrl;
    private static String administrationUrl;

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
}

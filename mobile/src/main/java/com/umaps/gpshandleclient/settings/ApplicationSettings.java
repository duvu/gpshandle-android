package com.umaps.gpshandleclient.settings;

import android.app.Application;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class ApplicationSettings extends Application {
    private static String serverURL; //--Include port
    private static String mappingUrl;
    private static String reportingUrl;
    private static String chartUrl;
    private static String administrationUrl;

    private static long timeInterval = 20000;

    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        ApplicationSettings.serverURL = serverURL;
        setMappingUrl(ApplicationSettings.serverURL+"/monitor");
        setAdministrationUrl(ApplicationSettings.serverURL+"/admin");
        setReportingUrl(ApplicationSettings.serverURL+"/report");
        setChartUrl(ApplicationSettings.serverURL+"/chart");
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

    public static String getChartUrl(){
        return chartUrl;
    }
    public static void setChartUrl(String chartUrl){
        ApplicationSettings.chartUrl = chartUrl;
    }

    public static String getAdministrationUrl() {
        return administrationUrl;
    }

    public static void setAdministrationUrl(String administrationUrl) {
        ApplicationSettings.administrationUrl = administrationUrl;
    }

    public static long getTimeInterval() {
        return (timeInterval > 10000 ? timeInterval: 20000);
    }

    public static void setTimeInterval(long timeInterval) {
        ApplicationSettings.timeInterval = timeInterval;
    }
}

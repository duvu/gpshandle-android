package com.umaps.gpshandleclient.settings;

import android.app.Application;
import android.app.ProgressDialog;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class SessionState extends Application {
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
}

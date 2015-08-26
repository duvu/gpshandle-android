package com.umaps.gpshandleclient;

import android.app.Application;

/**
 * Created by beou on 16/08/2015.
 */
public class Session extends Application {
    //-- to store all session-variables here
    //--1 account-name
    //--2 account-email
    //--3 account-profile-image
    //--4 user-name
    //--5 user-email
    //--6 user-profile-image
    //--7 session-token
    //--8 password
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

    public static String getAccountId() {
        return accountId;
    }

    public static void setAccountId(String accountId) {
        Session.accountId = accountId;
    }

    public static boolean isAccountManager() {
        return isAccountManager;
    }

    public static void setAccountManager(boolean manager) {
        Session.isAccountManager = manager;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        Session.userId = userId;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static void setDisplayName(String displayName) {
        Session.displayName = displayName;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        Session.description = description;
    }

    public static String getContactName() {
        return contactName;
    }

    public static void setContactName(String contactName) {
        Session.contactName = contactName;
    }

    public static String getContactPhone() {
        return contactPhone;
    }

    public static void setContactPhone(String contactPhone) {
        Session.contactPhone = contactPhone;
    }

    public static String getContactEmail() {
        return contactEmail;
    }

    public static void setContactEmail(String contactEmail) {
        Session.contactEmail = contactEmail;
    }

    public static long getCreationTime() {
        return creationTime;
    }

    public static void setCreationTime(long creationTime) {
        Session.creationTime = creationTime;
    }

    public static long getLastLoginTime() {
        return lastLoginTime;
    }

    public static void setLastLoginTime(long lastLoginTime) {
        Session.lastLoginTime = lastLoginTime;
    }

    public static String getUserProfileImage() {
        return userProfileImage;
    }

    public static void setUserProfileImage(String userProfileImage) {
        Session.userProfileImage = userProfileImage;
    }

    public static String getSessionToken() {
        return sessionToken;
    }

    public static void setSessionToken(String sessionToken) {
        Session.sessionToken = sessionToken;
    }

    public static long getTokenExpired() {
        return tokenExpired;
    }

    public static void setTokenExpired(long tokenExpired) {
        Session.tokenExpired = tokenExpired;
    }

    public static String getUserPassword() {
        return userPassword;
    }

    public static void setUserPassword(String userPassword) {
        Session.userPassword = userPassword;
    }

    public static int getTotalDevices() {
        return totalDevices;
    }

    public static void setTotalDevices(int totalDevices) {
        Session.totalDevices = totalDevices;
    }

    public static String getSelectedGroup() {
        return selectedGroup;
    }

    public static void setSelectedGroup(String selectedGroup) {
        Session.selectedGroup = selectedGroup;
    }

    public static int getGroupPosition() {
        return groupPosition < 0 ? 0 : groupPosition;
    }

    public static void setGroupPosition(int groupPosition) {
        Session.groupPosition = groupPosition;
    }

    public static int getSessionId() {
        return sessionId;
    }

    public static void setSessionId(int sessionId) {
        Session.sessionId = sessionId;
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
}

package com.umaps.gpssdk;

import android.content.Context;

/**
 * Created by beou on 16/08/2015.
 */
public class GpsSdk {
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
        // TODO: 26/08/2015 initialize gps-sdk
    }
}

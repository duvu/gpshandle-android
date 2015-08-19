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
    private static String userId;
    private static String userEmail; //will be accountEmail if user=admin
    private static String userProfileImage;
    private static String sessionToken;
    private static long tokenExpired;
    private static String userPassword;

    public static String getAccountId() {
        return accountId;
    }

    public static void setAccountId(String accountId) {
        Session.accountId = accountId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        Session.userId = userId;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        Session.userEmail = userEmail;
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
}

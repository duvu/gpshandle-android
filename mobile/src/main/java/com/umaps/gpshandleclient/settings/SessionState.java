package com.umaps.gpshandleclient.settings;

import android.app.Application;
import android.app.ProgressDialog;

import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.util.HTTPDelegateInterface;
import com.umaps.gpshandleclient.util.StringTools;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class SessionState extends Application {
    public static final String ACL_ADMIN_ACCOUNT        =   "acl.service.admin.account";
    private static ProgressDialog progressDialog;
    private static boolean isFleet = true;
    private static String subTitle;

    private static String accountID;
    private static String userID;
    private static String password;
    private static String groupID;

    private static String locale = "en";
    private static String sToken; //--

    private static String selDeviceID;
    private static Group selGroup;

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
        return selDeviceID;
    }

    public static void setSelDevice(String selDevice) {
        SessionState.selDeviceID = selDevice;
    }

    public static Group getSelGroup() {
        return selGroup;
    }

    public static void setSelGroup(Group selGroup) {
        SessionState.selGroup = selGroup;
        SessionState.groupID = selGroup.getGroupId();
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

    public static String getGroupID() {
        return (StringTools.isBlank(groupID)?"all":groupID);
    }

    public static void setGroupID(String groupID) {
        SessionState.groupID = groupID;
    }

    public static String getSelDeviceID() {
        return selDeviceID;
    }

    public static void setSelDeviceID(String selDeviceID) {
        SessionState.selDeviceID = selDeviceID;
    }

    static HTTPDelegateInterface delegate;
    public static void setDelegate(HTTPDelegateInterface httpDelegate){
        delegate = httpDelegate;
    }
    public static HTTPDelegateInterface getDelegate(){
        return delegate;
    }
}

package com.umaps.gpshandleclient.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class Utilities {
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

    public static final String SERVER_URL         = "serverUrl";

    public static final String IS_FLEET           = "isFleet";
    public static final String SUB_TITLE          = "subTitle";
    public static final String ACCOUNT_ID         = "accountID";
    public static final String USER_ID            = "userID";
    public static final String PASSWORD           = "password";
    public static final String LOCALE             = "locale";
    public static final String TOKEN              = "token";
    public static final String SEL_DEVICE         = "selDevice";
    public static final String SEL_GROUP          = "selGroup";
    public static final String SEL_GROUP_DESC     = "selGroupDesc";

    public static void populateSettings(Context context){
        //-- populate application-settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ApplicationSettings.setServerURL(prefs.getString(SERVER_URL, ""));

        SessionState.setIsFleet(prefs.getBoolean(IS_FLEET, true));
        SessionState.setSubTitle(prefs.getString(SUB_TITLE, ""));
        SessionState.setAccountID(prefs.getString(ACCOUNT_ID, ""));
        SessionState.setUserID(prefs.getString(USER_ID, ""));
        SessionState.setPassword(prefs.getString(PASSWORD, ""));
        SessionState.setLocale(prefs.getString(LOCALE, "en"));
        SessionState.setToken(prefs.getString(TOKEN, ""));
        SessionState.setSelDevice(prefs.getString(SEL_DEVICE, ""));
        SessionState.setSelGroup(prefs.getString(SEL_GROUP, ""));
        SessionState.setSelGroupDesc(prefs.getString(SEL_GROUP_DESC, ""));
    }
    public static void storeSettings(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(SEL_GROUP, SessionState.getSelGroup());
        editor.putString(SEL_GROUP_DESC, SessionState.getSelGroupDesc());
        editor.putString(SEL_DEVICE, SessionState.getSelDevice());
        editor.putString(TOKEN, SessionState.getToken());
        editor.putString(LOCALE, SessionState.getLocale());
        editor.putString(PASSWORD, SessionState.getPassword());
        editor.putString(USER_ID, SessionState.getUserID());
        editor.putString(ACCOUNT_ID, SessionState.getAccountID());

        editor.commit();
    }


    private static ProgressDialog pd;
    public static void ShowProgress(Context ctx, String title, String message) {
        if (ctx != null) {
            pd = new ProgressDialog(ctx, ProgressDialog.STYLE_HORIZONTAL);
            pd.setMax(100);
            pd.setIndeterminate(true);

            pd = ProgressDialog.show(ctx, title, message, true, true);
        }
    }

    public static void HideProgress() {
        if (pd != null) {
            pd.dismiss();
        }
    }





}

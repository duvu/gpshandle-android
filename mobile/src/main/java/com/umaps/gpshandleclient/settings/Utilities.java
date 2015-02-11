package com.umaps.gpshandleclient.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static void populateSettings(Context context){
        //-- populate application-settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ApplicationSettings.setServerURL(prefs.getString(SERVER_URL, ""));

//        SessionState.setAclAdminAccount(prefs.getInt(ACL_ADMIN_ACCOUNT, 0));
//        SessionState.setAclAdminDevice(prefs.getInt(ACL_ADMIN_DEVICE, 0));
//        SessionState.setAclAdminDriver(prefs.getInt(ACL_ADMIN_DRIVER, 0));
//        SessionState.setAclAdminGeozone(prefs.getInt(ACL_ADMIN_GEOZONE, 0));
//        SessionState.setAclAdminGroup(prefs.getInt(ACL_ADMIN_GROUP, 0));
//        SessionState.setAclAdminRole(prefs.getInt(ACL_ADMIN_ROLE, 0));
//        SessionState.setAclAdminRule(prefs.getInt(ACL_ADMIN_RULE, 0));
//        SessionState.setAclAdminUser(prefs.getInt(ACL_ADMIN_USER, 0));
//        SessionState.setAclAdminUserManager(prefs.getInt(ACL_ADMIN_USER_MANAGER, 0));
//
//        SessionState.setAclMapHistory(prefs.getInt(ACL_MAP_HISTORY, 0));
//        SessionState.setAclMapMonitor(prefs.getInt(ACL_MAP_MONITOR, 1));
//        SessionState.setAclReportDetail(prefs.getInt(ACL_REPORT_DETAIL, 0));
//        SessionState.setAclReportGeozone(prefs.getInt(ACL_REPORT_GEOZONE, 0));
//        SessionState.setAclReportParking(prefs.getInt(ACL_REPORT_PARKING, 0));
//        SessionState.setAclReportSummary(prefs.getInt(ACL_REPORT_SUMMARY, 0));


        SessionState.setIsFleet(prefs.getBoolean(IS_FLEET, true));
        SessionState.setSubTitle(prefs.getString(SUB_TITLE, ""));
        SessionState.setAccountID(prefs.getString(ACCOUNT_ID, ""));
        SessionState.setUserID(prefs.getString(USER_ID, ""));
        SessionState.setPassword(prefs.getString(PASSWORD, ""));
        SessionState.setLocale(prefs.getString(LOCALE, "en"));
        SessionState.setToken(prefs.getString(TOKEN, ""));
        SessionState.setSelDevice(prefs.getString(SEL_DEVICE, ""));
        SessionState.setSelGroup(prefs.getString(SEL_GROUP, ""));
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


    public static final String KEY_ACCOUNT          = "account";
    public static final String KEY_USER             = "user";
    public static final String KEY_PASSWORD         = "password";
    public static final String KEY_COMMAND          = "command";
    public static final String KEY_LOCALE           = "locale";
    public static final String KEY_PARAMS           = "params";

    public static final String KEY_REQUEST          = "requests";
    public static final String KEY_AUTHENTICATION   = "authentication";
    public static final String KEY_FIELDS           = "fields";
    public static final String KEY_DEV_FIELDS       = "dfields";

    public static final String KEY_RESULTS          = "results";
    public static final String KEY_STATUS           = "status";

    public static final String CMD_GET_USER_ACL     = "getUserAcl";
    public static final String CMD_GET_GROUPS       = "getGroups";
    public static final String CMD_GET_DEVICES      = "getDevices";
    public static final String CMD_GET_MAP_FLEET    = "getMapFleet";
    public static final String CMD_GET_MAP_DEVICE   = "getMapDevice";

    public static final String FLD_groupID          = "groupID";
    public static final String FLD_deviceID         = "deviceID";
    public static final String FLD_status           = "status";

    public static final String FLD_timeFrom         = "timeFrom";
    public static final String FLD_timeTo           = "timeTo";

    public static final String FLD_inclZones        = "inclZones";
    public static final String FLD_inclDebug        = "inclDebug";
    public static final String FLD_inclPOI          = "inclPOI";


    public static final String FLD_inclTime         = "inclTime";

    public static JSONObject createAuthenticationObject() throws JSONException {
        JSONObject jsonAuthentication = new JSONObject();
        jsonAuthentication.put(KEY_ACCOUNT, SessionState.getAccountID());
        jsonAuthentication.put(KEY_USER, SessionState.getUserID());
        jsonAuthentication.put(KEY_PASSWORD, SessionState.getPassword());
        return jsonAuthentication;
    }
    public static JSONObject createRequest(String command,
                                    String locale,
                                    JSONObject jsonParamsObject)
            throws JSONException
    {
        JSONObject jsonRequestObject = new JSONObject();
        jsonRequestObject.put(KEY_COMMAND, command);
        jsonRequestObject.put(KEY_LOCALE, locale);
        jsonRequestObject.put(KEY_PARAMS, jsonParamsObject);

        JSONObject request = new JSONObject();
        request.put(KEY_REQUEST, jsonRequestObject);
        request.put(KEY_AUTHENTICATION, createAuthenticationObject());
        return request;
    }
}

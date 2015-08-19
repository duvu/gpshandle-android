package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umaps.gpshandleclient.Session;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 03/06/2015.
 */
public class GpsRequest {
    private static final String TAG = "GpsOldRequest";
    public static final String BASE_URL                 = "https://secure.gpshandle.com:8443/ws";
    public static final String MAPPING_URL              = BASE_URL + "/monitor";
    public static final String REPORTING_URL            = BASE_URL + "/report";
    public static final String CHART_URL                = BASE_URL + "/chart";
    public static final String ADMIN_URL                = BASE_URL + "/admin";

    public static final String TOKEN_URL                = BASE_URL + "/token";
    public static final String CHART_STATE_URL          = BASE_URL + "/c/state?token=%s&groupID=%s";
    public static final String CHART_SUMMARY_URL        = BASE_URL + "/c/summary?token=%s&groupID=%s";

    public static final String KEY_ACCOUNT          = "account";
    public static final String KEY_USER             = "user";
    public static final String KEY_PASSWORD         = "password";
    public static final String KEY_COMMAND          = "command";
    public static final String KEY_LOCALE           = "locale";
    public static final String KEY_PARAMS           = "params";
    public static final String KEY_REQUEST          = "requests";
    public static final String KEY_AUTHENTICATION   = "authentication";

    public static final String KEY_ACCOUNT_ID      = "accountID";
    public static final String KEY_USER_ID         = "userID";

    public static final String CMD_GET_USER_ACL                 = "getUserAcl";
    public static final String CMD_GET_ACCOUNT                  = "getAccount";
    public static final String CMD_GET_AUTHORIZED_ACCOUNTS      = "getAuthorizedAccounts";
    public static final String CMD_GET_AUTHORIZED_USERS         = "getAuthorizedUsers";
    public static final String CMD_GET_USERS                    = "getUsers";
    public static final String CMD_GET_GROUPS                   = "getGroups";
    public static final String CMD_GET_DEVICES                  = "getDevices";
    public static final String CMD_GET_MAP_FLEET                = "getMapFleet";
    public static final String CMD_GET_MAP_DEVICE               = "getMapDevice";
    public static final String CMD_GET_CHART_SUMMARY            = "chartSummary";

    public static final String CMD_CREATE_DEVICE                = "createDevice";
    public static final String CMD_UPDATE_DEVICE                = "updateDevice";
    public static final String CMD_DELETE_DEVICE                = "deleteDevice";

    public static final String CMD_CREATE_GROUP                 = "createGroup";
    public static final String CMD_UPDATE_GROUP                 = "updateGroup";
    public static final String CMD_DELETE_GROUP                 = "deleteGroup";

    public static final String CMD_CREATE_USER                  = "createUser";
    public static final String CMD_UPDATE_USER                  = "updateUser";
    public static final String CMD_DELETE_USER                  = "deleteUser";

    private Context context;
    private HttpQueue mQueue;

    private int method;
    private String url;
    private String requestTag;

    private String accountID;
    private String userID;
    private String password;
    private String locale = "en";
    private String command;
    private JSONObject params;

    private Response.Listener<JSONObject> responseHandler;
    private Response.ErrorListener errorHandler;

    public GpsRequest(Context context) {
        this.context = context;
        mQueue = HttpQueue.getInstance(context);
    }

    public int getMethod() {
        return method;
    }

    public GpsRequest setMethod(int method) {
        this.method = method;
        return this;
    }
    public GpsRequest setPost(){
        return setMethod(Request.Method.POST);
    }
    public GpsRequest setGet() {
        return setMethod(Request.Method.GET);
    }
    public GpsRequest setDel() {
        return setMethod(Request.Method.DELETE);
    }
    public GpsRequest setPut() {
        return setMethod(Request.Method.PUT);
    }

    public String getUrl() {
        return url;
    }

    public String getRequestTag() {
        return requestTag;
    }

    public void setRequestTag(String requestTag) {
        this.requestTag = requestTag;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public Response.Listener<JSONObject> getResponseHandler() {
        return responseHandler;
    }

    public void setResponseHandler(Response.Listener<JSONObject> responseHandler) {
        this.responseHandler = responseHandler;
    }

    public Response.ErrorListener getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(Response.ErrorListener errorHandler) {
        this.errorHandler = errorHandler;
    }

    public JSONObject getAuthJson(){
        JSONObject authJson = new JSONObject();
        try {
            authJson.put(KEY_ACCOUNT, accountID);
            authJson.put(KEY_USER, userID);
            authJson.put(KEY_PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authJson;
    }
    public JSONObject getRequestJson(){
        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put(KEY_COMMAND, command);
            reqJson.put(KEY_PARAMS, params);
            reqJson.put(KEY_LOCALE, locale);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reqJson;
    }

    public JSONObject getJson(){
        if (method == Request.Method.POST) {
            JSONObject request = new JSONObject();
            try {
                request.put(KEY_REQUEST, getRequestJson());
                request.put(KEY_AUTHENTICATION, getAuthJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return request;
        } else if (method == Request.Method.GET){
            return null;
        }
        return null;
    }

    public void exec(){
        exec(getJson());
    }

    public void exec(JSONObject params){
        Log.i(TAG, url);
        JsonObjectRequest jObjReq = new JsonObjectRequest(method, url, params, responseHandler, errorHandler);
        if (!StringTools.isBlank(requestTag)){
            jObjReq.setTag(requestTag);
        }
        if (mQueue != null) {
            mQueue.addToRequestQueue(jObjReq);
        }
    }
    public void cancel(String tag){
        if (mQueue != null){
            mQueue.cancel(tag);
        }
    }

    public static GpsRequest getAccountRequest(Context context) {
        GpsRequest r = new GpsRequest(context);
        r.setAccountID(Session.getAccountId());
        r.setUserID(Session.getUserId());
        r.setPassword(Session.getUserPassword());
        r.setCommand(CMD_GET_ACCOUNT);
        r.setPost();
        r.setUrl(ADMIN_URL);
        return r;
    }
}

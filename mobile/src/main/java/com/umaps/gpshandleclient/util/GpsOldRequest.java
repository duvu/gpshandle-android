package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.model.MyResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 03/06/2015.
 */
public class GpsOldRequest {
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

    public static final String CMD_GET_USER_ACL     = "getUserAcl";
    public static final String CMD_GET_GROUPS       = "getGroups";
    public static final String CMD_GET_DEVICES      = "getDevices";
    public static final String CMD_GET_MAP_FLEET    = "getMapFleet";
    public static final String CMD_GET_MAP_DEVICE   = "getMapDevice";
    public static final String CMD_GET_CHART_SUMMARY = "chartSummary";

    private Context context;

    private int method;
    private String url;

    private String accountID;
    private String userID;
    private String password;
    private String locale = "en";
    private String command;
    private JSONObject params;

    private Response.Listener<JSONObject> responseHandler;
    private Response.ErrorListener errorHandler;

    public GpsOldRequest(Context context) {
        this.context = context;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
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

    /*private JSONObject createRequestToken(){
        JSONObject request = new JSONObject();
        try {
            request.put(KEY_ACCOUNT_ID, accountID);
            request.put(KEY_USER_ID, userID);
            request.put(KEY_PASSWORD, password);
            request.put(KEY_LOCALE, locale);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return request;
    }
    public void updateToken(){
        this.method = Request.Method.POST;
        this.url = TOKEN_URL;
        Response.Listener<JSONObject> tokeHanler = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()){
                    return;
                }
                String token = null;
                long expiredOn = 0;
                try {
                    token = ((JSONObject)mRes.getData()).getString("token");
                    expiredOn = ((JSONObject)mRes.getData()).getLong("expireOn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mApplication.setToken(token);
                mApplication.setExpireOn(expiredOn);
            }
        };
        Response.ErrorListener tokenErrorHandler = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
            }
        };
        JsonObjectRequest jObReq = new
                JsonObjectRequest(method, url, createRequestToken(), tokeHanler, tokenErrorHandler);
        HttpQueue.getInstance(context).addToRequestQueue(jObReq);
    }*/

    public void exec(){
        exec(getJson());
    }
    public void exec(JSONObject params){
        Log.i(TAG, url);
        JsonObjectRequest jObjReq =
                new JsonObjectRequest(method, url, params, responseHandler, errorHandler);
        HttpQueue.getInstance(context).addToRequestQueue(jObjReq);
    }
}

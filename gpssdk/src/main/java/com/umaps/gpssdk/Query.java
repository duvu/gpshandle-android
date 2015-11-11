package com.umaps.gpssdk;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umaps.gpssdk.http.HttpQueue;
import com.umaps.gpssdk.model.Account;
import com.umaps.gpssdk.model.GObject;
import com.umaps.gpssdk.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by beou on 03/06/2015.
 */
public class Query {
    private static final String TAG = "GpsRequest";
    public static final String BASE_URL                 = "https://apis.gpshandle.com:8443/ws";
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

    //-- a query included:
    //-- URL
    //-- Command
    //-- Method
    //-- Params
    private int method;
    private String url;
    private String command;
    private GObject params;
    public void FindInBackground(FindCallback callback) {

    }
    public void SaveInBackground(SaveCallback callback) {

    }
    public void DeleteInBackground(DeleteCallback callback) {

    }

    /*private static Query instance = null;
    public static Query getInstance() {
        if (instance == null) {
            instance = new Query();
        }
        return instance;
    }*/

    private HttpQueue mQueue;


    private String tag;

    private String accountID;
    private String userID;
    private String password;
    private String locale = "en";

    private Response.Listener<JSONObject> responseHandler;
    private Response.ErrorListener errorHandler;

    public Query() {
        mQueue = HttpQueue.getInstance(GpsSdk.getContext());
    }

    public int getMethod() {
        return method;
    }

    public Query setMethod(int method) {
        this.method = method;
        return this;
    }
    public Query setPost(){
        return setMethod(com.android.volley.Request.Method.POST);
    }
    public Query setGet() {
        return setMethod(com.android.volley.Request.Method.GET);
    }
    public Query setDel() {
        return setMethod(com.android.volley.Request.Method.DELETE);
    }
    public Query setPut() {
        return setMethod(com.android.volley.Request.Method.PUT);
    }

    public String getUrl() {
        return url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public void setErrorHandler(){
        this.errorHandler = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //noop
                //EventBus.getDefault().post();
                Log.e(TAG, "" + error.getMessage());
            }
        };
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
        if (method == com.android.volley.Request.Method.POST) {
            JSONObject request = new JSONObject();
            try {
                request.put(KEY_REQUEST, getRequestJson());
                request.put(KEY_AUTHENTICATION, getAuthJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return request;
        } else if (method == com.android.volley.Request.Method.GET){
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
        if (!StringTools.isBlank(tag)){
            jObjReq.setTag(tag);
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

    private static JSONObject getFleetParams(String groupId) {
        JSONObject p = new JSONObject();
        try {
            p.put(StringTools.FLD_status, null);
            p.put(StringTools.FLD_inclZones, false);
            p.put(StringTools.FLD_inclDebug, false);
            p.put(StringTools.FLD_inclPOI, false);
            p.put(StringTools.FLD_inclTime, false);
            p.put(StringTools.FLD_groupID, groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static JSONObject getMapDeviceParams(String deviceId, long from, long to) {
        JSONObject p = new JSONObject();
        try {
            p.put(StringTools.FLD_deviceID, deviceId);
            p.put(StringTools.FLD_status, null);
            p.put(StringTools.FLD_timeFrom, from/*1421127296*/);
            p.put(StringTools.FLD_timeTo, to/*1421137296*/);
            p.put(StringTools.FLD_inclZones, false);
            p.put(StringTools.FLD_inclDebug, false);
            p.put(StringTools.FLD_inclPOI, false);
            p.put(StringTools.FLD_inclTime, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    public void cancelAll() {
        cancel("common");
    }

    private static Query getCommonRequest() {
        Query r = new Query();
        r.setAccountID(GpsSdk.getAccountId());
        r.setUserID(GpsSdk.getUserId());
        r.setPassword(GpsSdk.getUserPassword());
        r.setTag("common");
        return r;
    }

    private static Query getAdminRequest() {
        Query r = getCommonRequest();
        r.setPost();
        r.setUrl(ADMIN_URL);
        return r;
    }

    private static Query getMapRequest() {
        Query r = getCommonRequest();
        r.setPost();
        r.setUrl(MAPPING_URL);
        r.setErrorHandler();
        return r;
    }

    public static Query getAccountRequest() {
        Query r = getAdminRequest();
        r.setCommand(CMD_GET_ACCOUNT);
        r.setErrorHandler();
        return r;
    }
    //-- get request for user
    public static Query getUserRequest() {
        Query r = getAdminRequest();
        r.setCommand(CMD_GET_USERS);
        r.setParams(User.createParams());
        r.setErrorHandler();
        return r;
    }

    public static Query getGroupRequest() {
        Query r = getAdminRequest();
        r.setCommand(CMD_GET_GROUPS);
        //r.setParams(Group.createGetParams());
        r.setErrorHandler();
        return r;
    }

    public static void getGroup(final Listener<MyResponse> listener) {
        Query r = getGroupRequest();
        r.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                listener.onResponse(mRes);
            }
        });
        r.exec();
    }
    public static void getUser(final Listener<MyResponse> listener) {
        Query r = getAdminRequest();
        r.setCommand(CMD_GET_USERS);
        r.setParams(User.createParams());
        r.setErrorHandler();
        r.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                JSONObject result = (JSONObject) mRes.getData();
                //-- store: contactPhone, contactName, contactEmail, description, displayName, creationTime, lastLoginTime
                User u = new User(result);
                GpsSdk.setDisplayName(u.getDisplayName());
                GpsSdk.setDescription(u.getDescription());
                GpsSdk.setContactName(u.getContactName());
                GpsSdk.setContactPhone(u.getContactPhone());
                GpsSdk.setContactEmail(u.getContactEmail());
                GpsSdk.setCreationTime(u.getCreationTime());
                GpsSdk.setLastLoginTime(u.getLastLoginTime());
                getGroup(listener);
            }
        });
        r.exec();
    }

    public static void getAccount(final Listener<MyResponse> listener) {
        Query r = getAdminRequest();
        r.setCommand(CMD_GET_ACCOUNT);
        r.setErrorHandler();

        String[] lf = new String[] {"isAccountManager", "deviceCount"};
        JSONObject accParam = Account.createParam(lf);
        r.setParams(accParam);
        r.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                JSONObject result = (JSONObject) mRes.getData();
                Account account = new Account(result);
                boolean isManager = account.isManager();
                int count = account.getDevice_count();
                GpsSdk.setAccountManager(isManager);
                GpsSdk.setTotalDevices(count);
                getUser(listener);
            }
        });
        r.exec();
    }
    public static void getAcls(final Listener<MyResponse> listener) {
        Query r = getAdminRequest();
        r.setCommand(CMD_GET_USER_ACL);
        JSONObject jsonParams = new JSONObject();
        r.setParams(jsonParams);
        r.setErrorHandler();
        r.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mResponse = new MyResponse(response);
                if (mResponse.isError()) {
                    return;
                }
                JSONArray aclList = (JSONArray) mResponse.getData();
                if (aclList == null) return;
                for (int i = 0; i < aclList.length(); i++) {
                    JSONObject acl = null;
                    try {
                        acl = aclList.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }
                    GpsSdk.storeAcls(acl);
                }
                getAccount(listener);
            }
        });
        r.exec();
    }

    public static void getToken(final Listener<MyResponse> listener) {
        Query r = getCommonRequest();
        r.setUrl(TOKEN_URL);
        r.setPost();
        r.setErrorHandler();
        r.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                String token = null;
                long expiredOn = 0;
                if (!mRes.isError()) {
                    try {
                        token = ((JSONObject) mRes.getData()).getString("token");
                        expiredOn = ((JSONObject) mRes.getData()).getLong("expireOn");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    GpsSdk.setSessionToken(token);
                    GpsSdk.setTokenExpired(expiredOn);
                    //get acl
                    getAcls(listener);
                }
            }
        });

        JSONObject params = new JSONObject();
        try {
            params.put(Query.KEY_ACCOUNT_ID, GpsSdk.getAccountId());
            params.put(Query.KEY_USER_ID, GpsSdk.getUserId());
            params.put(Query.KEY_PASSWORD, GpsSdk.getUserPassword());
            params.put(Query.KEY_LOCALE, Locale.getDefault().getLanguage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        r.exec(params);
    }

    public static Query getFleetRequest(Context context, String groupId) {
        Query r = getMapRequest();
        r.setCommand(CMD_GET_MAP_FLEET);
        r.setParams(getFleetParams(groupId));
        return r;
    }
    public static Query geMapsRequest(Context context, String deviceId, long from, long to) {
        Query r = getMapRequest();
        r.setCommand(CMD_GET_MAP_DEVICE);
        r.setParams(getMapDeviceParams(deviceId, from, to));
        r.setErrorHandler();
        return r;
    }


}

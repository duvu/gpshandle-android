package com.umaps.vtrack.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.umaps.vtrack.settings.ApplicationSettings;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beou on 29/10/2014.
 */
public class GpsHandleHTTPAsyncImpl extends Constants {
    private static final String LOG_TAG = "HTTPAsyncImpl";

    private HTTPDelegateInterface delegate = null;
    public GpsHandleHTTPAsyncImpl(HTTPDelegateInterface httpDelegateImpl){
        setDelegate(httpDelegateImpl);
    }

    public void getMapFleet(final Context context, final String accountID,
                            final String userID, final String password, final String locale,
                            final String groupID, final String[] status, final boolean inclZone,
                            final boolean inclDebug, final boolean inclPOI, final boolean inclTime)
            throws JSONException {
        JSONObject jsonPostObject = new JSONObject();
        //-- Authentication object
        jsonPostObject.put(KEY_AUTH, getJsonAuthObject(accountID, userID, password));
        //-- Params object
        JSONObject jsonParamsObject = new JSONObject();
        jsonParamsObject.put(FLD_groupID, groupID);
        jsonParamsObject.put(FLD_status, status);
        jsonParamsObject.put(FLD_inclZones, inclZone);
        jsonParamsObject.put(FLD_inclDebug, inclDebug);
        jsonParamsObject.put(FLD_inclPOI, inclPOI);
        jsonParamsObject.put(FLD_inclTime, inclTime);
        //-- Request object
        JSONObject jsonRequestObject = getJsonRequestObject(CMD_GET_MAP_FLEET, locale, jsonParamsObject);
        //-- build jsonPostObject
        jsonPostObject.put(KEY_REQUEST, jsonRequestObject);
        //--Do post server
        HTTPAsyncTask.getInstance().doPost(context, ApplicationSettings.getMappingUrl(),
                jsonPostObject, new JsonHttpResponseHandler(){
            @Override
            public void onStart(){}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                delegate.onFinish(response);
            }
//                    onFailure(int, Header[], Throwable, JSONObject
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable err, JSONObject response){
                Log.i(LOG_TAG, "getMapFleet failed");
                return;
            }
        });
    }

    public void getGroups(final Context context, final String accountID,
                          final String userID, final String password, String locale) throws JSONException
    {
        JSONObject jsonPostObject = new JSONObject();
        //-- Authentication object
        jsonPostObject.put(KEY_AUTH, getJsonAuthObject(accountID, userID, password));

        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<String>();
//        String[] fields = new String[]{
        fields.add("accountID");
        fields.add("groupID");
        fields.add("description");
        fields.add("pushpinID");
        fields.add("displayName");
        fields.add("deviceCount");
        fields.add("devicesList");
        List<String> dFields = new ArrayList<String>();
        dFields.add("deviceID");
        dFields.add("description");
        dFields.add("displayName");
        dFields.add("pushpinID");
        dFields.add("isActive");
        dFields.add("lastEventTimestamp");

        jsonParamsObject.put(KEY_FIELDS, new JSONArray(fields));
        jsonParamsObject.put(KEY_DEV_FIELDS, new JSONArray(dFields));
        JSONObject jsonRequestObject = getJsonRequestObject(CMD_GET_GROUPS, locale, jsonParamsObject);
        jsonPostObject.put(KEY_REQUEST, jsonRequestObject);

        HTTPAsyncTask.getInstance().doPost(context, ApplicationSettings.getAdministrationUrl(),
                jsonPostObject, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                delegate.onFinish(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(LOG_TAG, "HTTP Command Failed");
                return;
            }
        });
    }
    public void getDevices(final Context context, final String accountID,
                          final String userID, final String password, String locale, String groupID) throws JSONException
    {
        if (StringTools.isBlank(groupID)){
            groupID = Constants.GROUP_ALL;
        }
        JSONObject jsonPostObject = new JSONObject();
        //-- Authentication object
        jsonPostObject.put(KEY_AUTH, getJsonAuthObject(accountID, userID, password));
        //-- Params object
        JSONObject jsonParamsObject = new JSONObject();
        jsonParamsObject.put(FLD_groupID, groupID);
        JSONObject jsonRequestObject = getJsonRequestObject(CMD_GET_GROUPS, locale, jsonParamsObject);
        jsonPostObject.put(KEY_REQUEST, jsonRequestObject);

        HTTPAsyncTask.getInstance().doPost(context, ApplicationSettings.getAdministrationUrl(),
                jsonPostObject, new JsonHttpResponseHandler(){
            @Override
            public void onStart(){}
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                delegate.onFinish(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable err){
                return;
            }
        });
    }

    public void checkAuth(final Context context, final String accountID,
                          final String userID, final String password, String locale)
            throws JSONException {
        JSONObject jsonPostObject = new JSONObject();
        //-- Authentication object
        jsonPostObject.put(KEY_AUTH, getJsonAuthObject(accountID, userID, password));
        JSONObject jsonRequestObject = getJsonRequestObject(CMD_GET_USER_ACL, locale, null);

        jsonPostObject.put(KEY_REQUEST, jsonRequestObject);
        HTTPAsyncTask.getInstance().doPost(context, ApplicationSettings.getAdministrationUrl(),
                jsonPostObject, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                delegate.onFinish(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable err){
                Log.i("ON-FAILURE", "Get Error");
            }
        });
    }
    public void setDelegate(HTTPDelegateInterface delegate) {
        this.delegate = delegate;
    }

    public HTTPDelegateInterface getDelegate(){
        return this.delegate;
    }
    //-- create request Json Object
    private JSONObject getJsonRequestObject(String command, String locale, JSONObject jsonParamsObject) throws JSONException {
        JSONObject jsonRequestObject = new JSONObject();
        jsonRequestObject.put(KEY_COMMAND, command);
        jsonRequestObject.put(KEY_LOCALE, locale);
        jsonRequestObject.put(KEY_PARAMS, jsonParamsObject);
        return jsonRequestObject;
    }
    //-- create Authentication Json Object
    private JSONObject getJsonAuthObject(String accountID, String userID, String password) throws JSONException {
        JSONObject jsonAuthObject = new JSONObject();
        jsonAuthObject.put(KEY_ACCOUNT, accountID);
        jsonAuthObject.put(KEY_USER, userID);
        jsonAuthObject.put(KEY_PASSWORD, password);
        return jsonAuthObject;
    }
}

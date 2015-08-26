package com.umaps.gpssdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 27/01/2015.
 */
public class StringTools implements StringConstants {
    public static boolean isBlank(String s){
        return s == null || s.length() == 0;
    }

    public static JSONObject createAuthenticationObject(String accountID, String userID, String password) throws JSONException {
        JSONObject jsonAuthentication = new JSONObject();
        jsonAuthentication.put(KEY_ACCOUNT, accountID);
        jsonAuthentication.put(KEY_USER, userID);
        jsonAuthentication.put(KEY_PASSWORD, password);
        return jsonAuthentication;
    }
    public static JSONObject createRequest(String accountID, String userID, String password,
                                           String command, String locale,
                                           JSONObject jsonParamsObject)
            throws JSONException
    {
        JSONObject jsonRequestObject = new JSONObject();
        jsonRequestObject.put(KEY_COMMAND, command);
        jsonRequestObject.put(KEY_LOCALE, locale);
        jsonRequestObject.put(KEY_PARAMS, jsonParamsObject);

        JSONObject request = new JSONObject();
        request.put(KEY_REQUEST, jsonRequestObject);
        request.put(KEY_AUTHENTICATION, createAuthenticationObject(accountID, userID, password));
        return request;
    }
}

package com.umaps.gpshandleclient.model;

import android.util.Log;

import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 04/06/2015.
 */
public class MyResponse {
    public static final String KEY_RESULTS              = "results";
    public static final String KEY_STATUS               = "status";
    public static final String KEY_STATUS_CODE          = "code";
    public static final String KEY_STATUS_MESSAGE       = "message";
    public static final String CODE_SUCCESSFULL         = "SUCCESSFUL";
    public static final String CODE_NOT_AUTHORIZED      = "NOT_AUTHORIZED";

    private String code;
    private String message;
    private Object data;

    public MyResponse(JSONObject response) {
        try {
            JSONObject mStatus = response.has(KEY_STATUS) ? response.getJSONObject(KEY_STATUS) : null;
            if (mStatus!=null) {
                code    = mStatus.getString(KEY_STATUS_CODE);
                message = mStatus.getString(KEY_STATUS_MESSAGE);
            }
            data = response.has(KEY_RESULTS) ? response.get(KEY_RESULTS) : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MyResponse(String response){
        try {
            JSONObject mResponse = new JSONObject(response);
            if (mResponse!=null) {
                JSONObject mStatus = mResponse.has(KEY_STATUS) ? mResponse.getJSONObject(KEY_STATUS) : null;
                if (mStatus!=null) {
                    code = mStatus.has(KEY_STATUS_CODE) ? mStatus.getString(KEY_STATUS_CODE) : null;
                    message = mStatus.has(KEY_STATUS_MESSAGE) ? mStatus.getString(KEY_STATUS_MESSAGE) : null;
                }
                data = mResponse.has(KEY_RESULTS) ? mResponse.get(KEY_RESULTS) : null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isExpire() {
        if (StringTools.isBlank(this.code)) return true;
        if (this.code.equalsIgnoreCase(CODE_NOT_AUTHORIZED)) return true;
        return false;
    }

    public boolean isError(){
        if (StringTools.isBlank(this.code)) return true;
        if (this.code.equalsIgnoreCase(CODE_SUCCESSFULL)) return false;
        return true;
    }
    public boolean isSuccess(){
        if (StringTools.isBlank(this.code)) return false;
        if (this.code.equalsIgnoreCase(CODE_SUCCESSFULL)) return true;
        return false;
    }
}

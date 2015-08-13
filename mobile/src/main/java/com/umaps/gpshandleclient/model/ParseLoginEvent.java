package com.umaps.gpshandleclient.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by beou on 13/08/2015.
 */
@ParseClassName("LoginEvent")
public class ParseLoginEvent extends ParseObject {
    public static final String LOGIN_ACCOUNT        = "account";
    public static final String LOGIN_USER           = "user";
    public static final String LOGIN_TIMESTAMP      = "timestamp";
    public static final String LOGIN_STATUS         = "status";

    public String getLoginAccount() {
        return getString(LOGIN_ACCOUNT);
    }
    public void setLoginAccount(String account) {
        put(LOGIN_ACCOUNT, account);
    }

    public String getLoginUser() {
        return getString(LOGIN_USER);
    }
    public void setLoginUser(String user) {
        put(LOGIN_USER, user);
    }

    public long getTimestamp() {
        return getLong(LOGIN_TIMESTAMP);
    }
    public void setTimestamp(long timestamp) {
        put(LOGIN_TIMESTAMP, timestamp);
    }
    public boolean getStatus() {
        return getBoolean(LOGIN_STATUS);
    }
    public void setStatus(boolean status) {
        put(LOGIN_STATUS, status);
    }

}

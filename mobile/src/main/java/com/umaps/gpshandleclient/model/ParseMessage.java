package com.umaps.gpshandleclient.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by beou on 21/08/2015.
 */
@ParseClassName("Messages")
public class ParseMessage extends ParseObject {
    private static final String ACCOUNT_ID      = "accountId";
    private static final String USER_ID         = "userId";
    private static final String SUBJECT         = "subject";
    private static final String BODY            = "body";
    private static final String IS_READ         = "read";

    public String getAccountId() {
        return getString(ACCOUNT_ID);
    }
    public void setAccountId(String accountId) {
        put(ACCOUNT_ID, accountId);
    }
    public String getUserId(String userId) {
        return getString(USER_ID);
    }
    public void setUserId(String userId) {
        put(USER_ID, userId);
    }
    public String getSubject() {
        return getString(SUBJECT);
    }
    public void setSubject(String subject) {
        put(SUBJECT, subject);
    }
    public String getBody() {
        return getString(BODY);
    }
    public void setBody(String body) {
        put(BODY, body);
    }
    public boolean isRead() {
        return getBoolean(IS_READ);
    }
    public void setRead(boolean read) {
        put(IS_READ, read);
    }
    public void setRead() {
        setRead(true);
    }
    public void setUnread() {
        setRead(false);
    }
}

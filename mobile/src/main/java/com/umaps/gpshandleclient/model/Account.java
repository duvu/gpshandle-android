package com.umaps.gpshandleclient.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 09/06/2015.
 */
public class Account {
    private static final String ACCOUNT_ID          = "accountID";
    private static final String DESCRIPTION         = "description";
    private static final String DISPLAY_NAME        = "displayName";
    private static final String CONTACT_NAME        = "contactName";
    private static final String CONTACT_EMAIL       = "contactEmail";
    private static final String CONTACT_PHONE       = "contactPhone";
    private static final String DEVICE_COUNT        = "deviceCount";
    private static final String LAST_LOGIN_TIME     = "lastLoginTime";
    private static final String CREATION_TIME       = "creationTime";

    private String id;
    private String description;
    private String display_name;
    private String contact_name;
    private String contact_email;
    private String contact_phone;
    private int device_count;
    private long last_login_time;
    private long creation_time;

    public Account(JSONObject account){
        try {
            this.id = account.getString(ACCOUNT_ID);
            this.description = account.getString(DESCRIPTION);
            this.display_name = account.getString(DISPLAY_NAME);
            this.contact_name = account.getString(CONTACT_NAME);
            this.contact_email = account.getString(CONTACT_EMAIL);
            this.contact_phone = account.getString(CONTACT_PHONE);
            this.device_count = account.getInt(DEVICE_COUNT);
            this.last_login_time = account.getLong(LAST_LOGIN_TIME);
            this.creation_time = account.getLong(CREATION_TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public int getDevice_count() {
        return device_count;
    }

    public void setDevice_count(int device_count) {
        this.device_count = device_count;
    }

    public long getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(long last_login_time) {
        this.last_login_time = last_login_time;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }
}

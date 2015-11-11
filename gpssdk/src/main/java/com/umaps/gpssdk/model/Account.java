package com.umaps.gpssdk.model;

import com.umaps.gpssdk.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private static final String IS_MANAGER          = "isAccountManager";

    private String id;
    private String description;
    private String display_name;
    private String contact_name;
    private String contact_email;
    private String contact_phone;
    private int device_count;
    private long last_login_time;
    private long creation_time;
    private boolean isManager;

    public Account(JSONObject account){
        try {
            this.id                 = account.has(ACCOUNT_ID) ? account.getString(ACCOUNT_ID) : "";
            this.description        = account.has(DESCRIPTION) ? account.getString(DESCRIPTION) : "";
            this.display_name       = account.has(DISPLAY_NAME) ? account.getString(DISPLAY_NAME) : "";
            this.contact_name       = account.has(CONTACT_NAME) ? account.getString(CONTACT_NAME) : "";
            this.contact_email      = account.has(CONTACT_EMAIL) ? account.getString(CONTACT_EMAIL) : "";
            this.contact_phone      = account.has(CONTACT_PHONE) ? account.getString(CONTACT_PHONE) : "";
            this.device_count       = account.has(DEVICE_COUNT) ? account.getInt(DEVICE_COUNT) : 0;
            this.last_login_time    = account.has(LAST_LOGIN_TIME) ? account.getLong(LAST_LOGIN_TIME) : 0L;
            this.creation_time      = account.has(CREATION_TIME) ? account.getLong(CREATION_TIME) : 0L;
            this.isManager          = account.has(IS_MANAGER) ? account.getBoolean(IS_MANAGER) : false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Account> getListFromJson(JSONArray jsonArray){
        ArrayList<Account> accounts = new ArrayList<Account>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Account account = null;
            try {
                account = new Account((JSONObject) jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (account!=null) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    public static JSONObject createParam(String[] fields) {
        JSONObject params = new JSONObject();
        List<String> lFields = new ArrayList<>();
        for (int i = 0; i < fields.length; i++){
            lFields.add(fields[i]);
        }
        try {
            return  params.put(StringTools.KEY_FIELDS, new JSONArray(lFields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject createParam(){
        List<String> fields = new ArrayList<>();
        fields.add(ACCOUNT_ID);
        fields.add(DESCRIPTION);
        fields.add(DISPLAY_NAME);
        fields.add(CONTACT_NAME);
        fields.add(CONTACT_EMAIL);
        fields.add(CONTACT_PHONE);
        fields.add(DEVICE_COUNT);
        fields.add(LAST_LOGIN_TIME);
        fields.add(CREATION_TIME);
        JSONObject params = new JSONObject();
        try {
            params.put(StringTools.KEY_FIELDS, new JSONArray(fields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
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

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean isManager) {
        this.isManager = isManager;
    }
}

package com.umaps.gpshandleclient.model;

import android.content.Context;

import com.android.volley.Request;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.Session;
import com.umaps.gpshandleclient.util.GpsRequest;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beou on 10/06/2015.
 */
public class User {
    private static final String USER_ID         = "userID";
    private static final String PASSWORD        = "password";
    private static final String DESCRIPTION     = "description";
    private static final String DISPLAY_NAME    = "displayName";
    private static final String IS_ACTIVE       = "isActive";
    private static final String CONTACT_NAME    = "contactName";
    private static final String CONTACT_EMAIL   = "contactEmail";
    private static final String CONTACT_PHONE   = "contactPhone";
    private static final String CONTACT_GENDER  = "gender";
    private static final String LAST_LOGIN_TIME = "lastLoginTime";
    private static final String CREATION_TIME   = "creationTime";
    private static final String MANAGED_GROUPS  = "managedGroups";

    private String id;
    private String password;
    private String description;
    private String displayName;
    private boolean isActive;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String contactGender;
    private long lastLoginTime;
    private long creationTime;

    private ArrayList<Group> groupList;

    private Context context;
    private MyApplication mApplication;

    public User(JSONObject jsonUser) {
        try {
            this.id             = jsonUser.has(USER_ID)         ? jsonUser.getString(USER_ID)       : "";
            this.password       = jsonUser.has(PASSWORD)        ? jsonUser.getString(PASSWORD)      : "";
            this.description    = jsonUser.has(DESCRIPTION)     ? jsonUser.getString(DESCRIPTION)   : "";
            this.displayName    = jsonUser.has(DISPLAY_NAME)    ? jsonUser.getString(DISPLAY_NAME)  : "";
            this.isActive       = jsonUser.has(IS_ACTIVE)       ? jsonUser.getBoolean(IS_ACTIVE)    : false;
            this.contactName    = jsonUser.has(CONTACT_NAME)    ? jsonUser.getString(CONTACT_NAME)  : "";
            this.contactEmail   = jsonUser.has(CONTACT_EMAIL)   ? jsonUser.getString(CONTACT_EMAIL) : "";
            this.contactPhone   = jsonUser.has(CONTACT_PHONE)   ? jsonUser.getString(CONTACT_PHONE) : "";
            this.contactGender  = jsonUser.has(CONTACT_GENDER)  ? jsonUser.getString(CONTACT_GENDER): "";
            this.lastLoginTime  = jsonUser.has(LAST_LOGIN_TIME) ? jsonUser.getLong(LAST_LOGIN_TIME) : 0L;
            this.creationTime   = jsonUser.has(CREATION_TIME)   ? jsonUser.getLong(CREATION_TIME)   : 0L;

            Object jo = jsonUser.has(MANAGED_GROUPS) ? jsonUser.get(MANAGED_GROUPS) : null;
            if (jo != null && jo instanceof JSONArray) {
                JSONArray ja = (JSONArray) jo;
                if (ja.length() > 0) {
                    groupList = new ArrayList<>();
                    for (int i = 0; i < ja.length(); i++){
                        groupList.add(new Group(ja.getJSONObject(i)));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(Context context) {
        this.context = context;
        mApplication = MyApplication.getInstance();
    }

    public static JSONObject createParams(){
        List<String> fields = new ArrayList<>();
        fields.add(USER_ID);
        fields.add(DESCRIPTION);
        fields.add(DISPLAY_NAME);
        fields.add(IS_ACTIVE);
        fields.add(CONTACT_NAME);
        fields.add(CONTACT_EMAIL);
        fields.add(CONTACT_PHONE);
        fields.add(CONTACT_GENDER);
        fields.add(LAST_LOGIN_TIME);
        fields.add(CREATION_TIME);
        fields.add(MANAGED_GROUPS);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return (description!=null)?description:getDisplayName();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return (displayName!=null)?displayName:description;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactGender() {
        return contactGender;
    }

    public void setContactGender(String contactGender) {
        this.contactGender = contactGender;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }
    public void setLastLoginTime(long llTime) {
        this.lastLoginTime = llTime;
    }

    public long getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(long ctime) {
        this.creationTime = ctime;
    }

    public ArrayList<Group> getGroupList() {
        return this.groupList;
    }

    public void setGroupList(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public GpsRequest getRequestCreate() {
        GpsRequest r = new GpsRequest(context);
        if (mApplication == null) {
            mApplication = MyApplication.getInstance();
        }
        r.setAccountID(Session.getAccountId());
        r.setUserID(Session.getUserId());
        r.setPassword(Session.getUserPassword());
        r.setLocale(mApplication.getLocale());
        r.setUrl(GpsRequest.ADMIN_URL);
        r.setMethod(Request.Method.POST);
        r.setCommand(GpsRequest.CMD_CREATE_USER);
        r.setParams(buildParams());
        return r;
    }
    public GpsRequest getRequestEdit() {
        GpsRequest r = new GpsRequest(context);
        if (mApplication == null) {
            mApplication = MyApplication.getInstance();
        }
        r.setAccountID(Session.getAccountId());
        r.setUserID(Session.getUserId());
        r.setPassword(Session.getUserPassword());
        r.setLocale(mApplication.getLocale());
        r.setUrl(GpsRequest.ADMIN_URL);
        r.setMethod(Request.Method.POST);
        r.setCommand(GpsRequest.CMD_UPDATE_USER);
        r.setParams(buildParams());
        return r;
    }
    public GpsRequest getRequestDelete() {
        GpsRequest r = new GpsRequest(context);
        if (mApplication == null) {
            mApplication = MyApplication.getInstance();
        }
        r.setAccountID(Session.getAccountId());
        r.setUserID(Session.getUserId());
        r.setPassword(Session.getUserPassword());
        r.setLocale(mApplication.getLocale());
        r.setUrl(GpsRequest.ADMIN_URL);
        r.setMethod(Request.Method.POST);
        r.setCommand(GpsRequest.CMD_DELETE_USER);
        r.setParams(buildParams());
        return r;
    }

    private JSONObject buildParams(){
        JSONObject j = new JSONObject();
        try {
            j.put(USER_ID, this.getId());               //-- *
            j.put(PASSWORD, this.getPassword());        //-- *
            j.put(DESCRIPTION, this.getDescription());
            j.put(DISPLAY_NAME, this.getDisplayName());
            j.put(IS_ACTIVE, this.isActive);
            j.put(CONTACT_NAME, this.contactName);
            j.put(CONTACT_EMAIL, this.contactEmail);    //-- *
            j.put(CONTACT_PHONE, this.contactPhone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j;
    }
}

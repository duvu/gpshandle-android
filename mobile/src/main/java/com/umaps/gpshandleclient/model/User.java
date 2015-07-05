package com.umaps.gpshandleclient.model;

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
    private static final String DESCRIPTION     = "description";
    private static final String DISPLAY_NAME    = "displayName";
    private static final String CONTACT_NAME    = "contactName";
    private static final String CONTACT_EMAIL   = "contactEmail";
    private static final String CONTACT_PHONE   = "contactPhone";
    private static final String CONTACT_GENDER  = "gender";
    private static final String LAST_LOGIN_TIME = "lastLoginTime";
    private static final String CREATION_TIME   = "creationTime";
    private static final String MANAGED_GROUPS  = "managedGroups";

    private String id;
    private String description;
    private String displayName;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String contactGender;
    private long lastLoginTime;
    private long creationTime;

    private ArrayList<Group> groupList;


    public User(JSONObject jsonUser) {
        try {
            this.id             = jsonUser.getString(USER_ID);
            this.description    = jsonUser.getString(DESCRIPTION);
            this.displayName    = jsonUser.getString(DISPLAY_NAME);
            this.contactName    = jsonUser.getString(CONTACT_NAME);
            this.contactEmail   = jsonUser.getString(CONTACT_EMAIL);
            this.contactPhone   = jsonUser.getString(CONTACT_PHONE);
            this.contactGender  = jsonUser.getString(CONTACT_GENDER);
            this.lastLoginTime  = jsonUser.getLong(LAST_LOGIN_TIME);
            this.creationTime   = jsonUser.getLong(CREATION_TIME);

            JSONArray lGrp = jsonUser.getJSONArray(MANAGED_GROUPS);
            if ((lGrp!=null) && (lGrp.length() > 0)) {
                groupList = new ArrayList<>();
                for (int i = 0; i < lGrp.length(); i++){
                    groupList.add(new Group(lGrp.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject createParams(){
        List<String> fields = new ArrayList<>();
        fields.add(USER_ID);
        fields.add(DESCRIPTION);
        fields.add(DISPLAY_NAME);
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
}

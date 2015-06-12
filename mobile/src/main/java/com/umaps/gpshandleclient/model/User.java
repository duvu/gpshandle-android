package com.umaps.gpshandleclient.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 10/06/2015.
 */
public class User {
    private static final String USER_ID = "userID";
    private static final String DESCRIPTION = "description";
    private static final String DISPLAY_NAME = "displayName";
    private static final String CONTACT_NAME = "contactName";
    private static final String CONTACT_EMAIL = "contactEmail";
    private static final String CONTACT_PHONE = "contactPhone";
    private static final String CONTACT_GENDER = "gender";
    private String id;
    private String description;
    private String displayName;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String contactGender;

    public User(JSONObject jsonUser) {
        try {
            this.id = jsonUser.getString(USER_ID);
            this.description = jsonUser.getString(DESCRIPTION);
            this.displayName = jsonUser.getString(DISPLAY_NAME);
            this.contactName = jsonUser.getString(CONTACT_NAME);
            this.contactEmail = jsonUser.getString(CONTACT_EMAIL);
            this.contactPhone = jsonUser.getString(CONTACT_PHONE);
            this.contactGender = jsonUser.getString(CONTACT_GENDER);
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

    public String getDisplayName() {
        return displayName;
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
}

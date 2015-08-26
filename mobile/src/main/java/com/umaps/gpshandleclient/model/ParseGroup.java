package com.umaps.gpshandleclient.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beou on 20/08/2015.
 */
@ParseClassName("ParseGroup")
public class ParseGroup extends ParseObject {
    private static final String ACCOUNT_ID          = "accountID";
    private static final String GROUP_ID            = "groupID";
    private static final String DESCRIPTION         = "description";
    private static final String DISPLAY_NAME        = "displayName";
    private static final String DEVICE_COUNT        = "deviceCount";
    private static final String DEVICE_LIST         = "devicesList";
    private static final String PUSHPIN_ID          = "pushpinID";
    private static final String NOTES               = "notes";
    private static final String LAST_UPDATE_TIME    = "lastUpdateTime";
    private static final String CREATION_TIME       = "creationTime";

    public ParseGroup() {}

    public static ParseGroup parseGroup (JSONObject j) throws JSONException {
        if (j==null) return null;
        ParseGroup p = new ParseGroup();
        List<ParseDevice> list;
        if (j.has(ACCOUNT_ID))          {p.setAccountId     (j.getString(ACCOUNT_ID));}
        if (j.has(GROUP_ID))            {p.setGroupId       (j.getString(GROUP_ID));}
        if (j.has(DESCRIPTION))         {p.setDescription   (j.getString(DESCRIPTION));}
        if (j.has(DISPLAY_NAME))        {p.setDisplayName   (j.getString(DISPLAY_NAME));}
        if (j.has(DEVICE_COUNT))        {p.setDeviceCount   (j.getInt(DEVICE_COUNT));}

        if (j.has(DEVICE_LIST)) {
            list = new ArrayList<>();
            JSONArray ja = j.getJSONArray(DEVICE_LIST);
            for (int i = 0; i< ja.length(); i++) {
                ParseDevice pd = ParseDevice.parseDevice(ja.getJSONObject(i));
                list.add(pd);
            }
            p.setDeviceList(list);
        }
        if (j.has(PUSHPIN_ID))          {p.setPushpinId     (j.getString(PUSHPIN_ID));}
        if (j.has(NOTES))               {p.setNotes         (j.getString(NOTES));}
        if (j.has(LAST_UPDATE_TIME))    {p.setLastUpdateTime(j.getLong(LAST_UPDATE_TIME));}
        if (j.has(CREATION_TIME))       {p.setCreationTime  (j.getLong(CREATION_TIME));}
        return p;
    }

    public static ParseGroup getGroupAll() {
        ParseGroup p = new ParseGroup();
        p.setGroupId("all");
        p.setDescription("All");
        p.setDisplayName("All");
        return p;
    }

    public String getAccountId() {
        return getString(ACCOUNT_ID);
    }
    public void setAccountId(String accountId) {
        put(ACCOUNT_ID, accountId);
    }

    public String getGroupId() {
        return getString(GROUP_ID);
    }
    public void setGroupId(String groupId) {
        put(GROUP_ID, groupId);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }
    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public String getDisplayName() {
        return getString(DISPLAY_NAME);
    }

    public String getName() {
        if (StringTools.isBlank(getDisplayName())) {
            return getDescription();
        } else {
            return getDisplayName();
        }
    }

    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }

    public int getDeviceCount() {
        return getInt(DEVICE_COUNT);
    }

    public void setDeviceCount(int deviceCount) {
        put(DEVICE_COUNT, deviceCount);
    }

    public List<ParseDevice> getDeviceList() {
        return getList(DEVICE_LIST);
    }

    public void setDeviceList(List<ParseDevice> list) {
        put(DEVICE_LIST, list);
    }

    public String getPushpinId(){
        return getString(PUSHPIN_ID);
    }
    public void setPushpinId(String pushpinId) {
        put(PUSHPIN_ID, pushpinId);
    }

    public String getNotes(){
        return getString(NOTES);
    }
    public void setNotes(String notes) {
        put(NOTES, notes);
    }
    public long getLastUpdateTime() {
        return getLong(LAST_UPDATE_TIME);
    }
    public void setLastUpdateTime(long time) {
        put(LAST_UPDATE_TIME, time);
    }

    public long getCreationTime() {
        return getLong(CREATION_TIME);
    }
    public void setCreationTime(long time) {
        put(CREATION_TIME, time);
    }
}

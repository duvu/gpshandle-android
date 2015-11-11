package com.umaps.gpssdk.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * GPSHandleClient
 * com.umaps.gpssdk
 * Created by beou on 11/11/2015.
 */
public class GObject extends JSONObject {

    @Override
    public GObject put(String key, Object value) {
        try {
            super.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public GObject put(String key, String value) {
        try {
            super.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public GObject put(String key, int value) {
        try {
            super.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public GObject put (String key, double value) {
        try {
            super.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public GObject put(String key, boolean value) {
        try {
            super.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public GObject put(String key, long value) {
        try {
            super.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public void put (String key, List<GObject> listObjects) {
        //put-json-array
        if (listObjects == null) return;
        JSONArray jsonArray = new JSONArray();
        for (GObject o : listObjects) {
            jsonArray.put(o);
        }
        try {
            super.put(key, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<GObject> getObjects(String key) {
        JSONArray jsonArray = null;
        try {
            jsonArray = super.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray==null) return null;
        List<GObject> lisObjects = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                lisObjects.add((GObject) jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lisObjects;
    }
    //--get*
    public String getString(String key) {
        try {
            return super.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getInt(String key) {
        try {
            return super.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public long getLong(String key) {
        try {
            return super.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0L;
        }
    }
    public double getDouble(String key) {
        try {
            return super.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0.0D;
        }
    }
    public GObject getObject(String key) {
        try {
            return (GObject)super.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean getBoolean(String key) {
        try {
            return super.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.umaps.gpshandleclient.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by beou on 30/10/2014.
 */
public class PrivateStore {
    private static final String V9TrackSharedStore = "V9TrackSharedStore";
    private SharedPreferences sharedPreferences;
    //private SharedPreferences.Editor editor;

    public PrivateStore(Context context){
        SharedPreferences _sPrf = context.getSharedPreferences(V9TrackSharedStore, Context.MODE_PRIVATE);
        this.sharedPreferences = _sPrf;
        //this.editor = _sPrf.edit();
    }
    public PrivateStore(SharedPreferences _share){
        this.sharedPreferences = _share;
        //this.editor = _share.edit();
    }

    public String getString(String s, String s2) {
        return (sharedPreferences.contains(s))?sharedPreferences.getString(s, s2):s2;
    }
    public int getInt(String s, int i) {
        return (sharedPreferences.contains(s) ? sharedPreferences.getInt(s, i) : i);
    }
    public long getLong(String s, long l) {
        return (sharedPreferences.contains(s) ? sharedPreferences.getLong(s, l) : l);
    }
    public float getFloat(String s, float v) {
        return sharedPreferences.contains(s) ? sharedPreferences.getFloat(s, v) : v;
    }
    public boolean getBoolean(String s, boolean b) {
        return sharedPreferences.contains(s) ? sharedPreferences.getBoolean(s, b) : b;
    }

    public void setString(String key, String value){
        if(key==null || value==null){return;}
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void remove(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public void setInt(String key, int value){
        if(key==null){return;}
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public void setLong(String key, long value){
        if(key == null)return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key,value);
        editor.commit();
    }
    public void setFload(String key, float value){
        if(key==null)return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key,value);
        editor.commit();
    }
    public void setBoolean(String key, boolean value){
        if(key==null)return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}

package com.umaps.vtrack.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class Utilities {
    public static void populateSettings(Context context){
        //-- populate application-settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
}

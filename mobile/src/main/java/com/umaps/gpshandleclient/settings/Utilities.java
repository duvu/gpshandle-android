package com.umaps.gpshandleclient.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.umaps.gpshandleclient.settings.ApplicationSettings;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class Utilities {
    public static void populateSettings(Context context){
        //-- populate application-settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ApplicationSettings.setServerURL(
                prefs.getString(ApplicationSettings.KEY_URL, ApplicationSettings.NULL_STRING));
        ApplicationSettings.setsAccount(
                prefs.getString(ApplicationSettings.KEY_ACCOUNT, ApplicationSettings.NULL_STRING));
        ApplicationSettings.setsUser(
                prefs.getString(ApplicationSettings.KEY_USER, ApplicationSettings.NULL_STRING));
        ApplicationSettings.setsPassword(
                prefs.getString(ApplicationSettings.KEY_PASSWORD, ApplicationSettings.NULL_STRING));
    }
}

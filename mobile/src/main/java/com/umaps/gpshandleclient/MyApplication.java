package com.umaps.gpshandleclient;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.umaps.gpshandleclient.model.ParseDevice;
import com.umaps.gpshandleclient.model.ParseGroup;
import com.umaps.gpshandleclient.model.ParseLoginEvent;
import com.umaps.gpssdk.GpsSdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication instance = null;
    private static Typeface mIconFont = null;
    private static Typeface mTextFont = null;



    private static final String LOCALE                  = "locale";
    private static final String SEL_GROUP_DESC          = "selGroupDesc";
    private static final String SEL_DEVICE              = "selDevice";
    private static final String SEL_DEVICE_DESC         = "selDeviceDesc";
    private static final String IS_FLEET                = "isFleet";
    private static final String LOADED_GROUP            = "loadedGroupData";
    private static final String LOADED_DEVICE           = "loadedDeviceData";
    private static final String IS_SIGNED_IN            = "isSignedIn";

    public static MyApplication getInstance(){
        return instance;
    }
    public static Typeface getIconFont(){
        return mIconFont;
    }
    public static Typeface getTextFont(){
        return mTextFont;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //-- register class
        ParseObject.registerSubclass(ParseLoginEvent.class);
        ParseObject.registerSubclass(ParseGroup.class);
        ParseObject.registerSubclass(ParseDevice.class);
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        ParseUser.enableAutomaticUser();
        Parse.initialize(this);

        ParsePush.subscribeInBackground("GPS");

        ParseInstallation.getCurrentInstallation().saveInBackground();

        instance = this;
        mIconFont = Typeface.createFromAsset(this.getAssets(), "icomoon.ttf");
        mTextFont = Typeface.createFromAsset(this.getAssets(), "OpenSans-Regular.ttf");

        GpsSdk.initialize(this);
        populateSettings();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GpsSdk.saveInstanceState();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        storeSettings();
        GpsSdk.saveInstanceState();
    }

    public long timeInterval    = 20000;
    private boolean isFleet     = true;
    private String locale = "en";
    private String selDevice;
    private String selDeviceDesc;

    private String groupList;

    private boolean isSignedIn;

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public boolean isFleet() {
        return isFleet;
    }

    public void setIsFleet(boolean isFleet) {
        this.isFleet = isFleet;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSelDevice() {
        return selDevice;
    }

    public void setSelDevice(String selDevice) {
        this.selDevice = selDevice;
    }

    public String getSelDeviceDesc() {
        return selDeviceDesc;
    }

    public void setSelDeviceDesc(String selDeviceDesc) {
        this.selDeviceDesc = selDeviceDesc;
    }

    public String getGroupList() {
        return groupList;
    }

    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }

    public boolean isSignedIn() {
        return isSignedIn;
    }

    public void setIsSignedIn(boolean isSignedIn) {
        this.isSignedIn = isSignedIn;
    }

    public void populateSettings(){
        //SharedPreferences prefs = this.getSharedPreferences(GPS_HANDLE_CLIENT, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.setLocale(prefs.getString(LOCALE, "en"));
        this.setSelDevice(prefs.getString(SEL_DEVICE, ""));
        this.setSelDeviceDesc(prefs.getString(SEL_DEVICE_DESC, ""));
        this.setIsFleet(prefs.getBoolean(IS_FLEET, true));
        this.setGroupList(prefs.getString(LOADED_GROUP, ""));
        this.setIsSignedIn(prefs.getBoolean(IS_SIGNED_IN, false));
    }

    public void storeSettings(){
        //SharedPreferences prefs = this.getSharedPreferences(GPS_HANDLE_CLIENT, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(LOCALE, this.getLocale());
        editor.putString(SEL_DEVICE, this.getSelDevice());
        editor.putString(SEL_DEVICE_DESC, this.getSelDeviceDesc());
        editor.putBoolean(IS_FLEET, this.isFleet());
        editor.putString(LOADED_GROUP, this.getGroupList());
        editor.putBoolean(IS_SIGNED_IN, this.isSignedIn());
        editor.commit();
        GpsSdk.saveInstanceState();
    }
}

package com.umaps.gpshandleclient.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.cluster.TrackInfoWindowAdapter;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.util.GpsOldRequest;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends ActionBarActivity
    implements TrackInfoWindowAdapter.TrackInfoWindowCallback {
    private static final String TAG = "MainActivity";
    Typeface mTf = null;
    private MyApplication mApplication;
    private GpsOldRequest mRequestGetGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApplication = MyApplication.getInstance();
        mTf = mApplication.getIconFont();

        if (!isUserSignedIn()){
            startLoginActivity();
            finish();
        }
        mApplication.setIsFleet(true);

        mRequestGetGroup = new GpsOldRequest(this);
        mRequestGetGroup.setAccountID(mApplication.getAccountID());
        mRequestGetGroup.setUserID(mApplication.getUserID());
        mRequestGetGroup.setPassword(mApplication.getPassword());
        mRequestGetGroup.setMethod(Request.Method.POST);
        mRequestGetGroup.setUrl(GpsOldRequest.ADMIN_URL);
        mRequestGetGroup.setCommand(GpsOldRequest.CMD_GET_GROUPS);
        JSONObject params = createParams();
        mRequestGetGroup.setParams(params);
        mRequestGetGroup.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mApplication.setGroupList(response.toString());
            }
        });
        mRequestGetGroup.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
            }
        });
        mRequestGetGroup.exec();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        //-- set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        int h = getSupportActionBar().getHeight();
        h = (h < 60) ? 60 : h;
        Drawable dr = getResources().getDrawable(R.drawable.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable)dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, h, h, true));
        toolbar.setNavigationIcon(d);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View monitor = findViewById(R.id.monitor);
        TextView icMonitor = (TextView) findViewById(R.id.ic_monitor);
        TextView txtMonitor = (TextView) findViewById(R.id.txt_monitor);
        icMonitor.setTypeface(mTf);
        icMonitor.setText(String.valueOf((char) 0xe706));
        txtMonitor.setText(R.string.title_monitor);

        View report = findViewById(R.id.report);
        TextView icReport = (TextView) findViewById(R.id.ic_report);
        TextView txtReport = (TextView) findViewById(R.id.txt_report);
        icReport.setTypeface(mTf);
        icReport.setText(String.valueOf((char) 0xe63b));
        txtReport.setText(R.string.title_report);

        TextView icAdmin = (TextView) findViewById(R.id.ic_admin);
        TextView txtAdmin = (TextView) findViewById(R.id.txt_admin);
        icAdmin.setTypeface(mTf);
        icAdmin.setText(String.valueOf((char) 0xe62a));
        txtAdmin.setText(R.string.title_administration);

        monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MapFragment.newInstance())
                        .addToBackStack("realTime").commit();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ReportPager.newInstance())
                        .commit();
            }
        });

        icAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Administration", Toast.LENGTH_LONG).show();
            }
        });
        txtAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Administration", Toast.LENGTH_LONG).show();
            }
        });


        //--Settings
        final TextView tvMenu = (TextView) findViewById(R.id.btn_settings);
        TextView tvMenuText = (TextView) findViewById(R.id.btn_settings_text);
        tvMenu.setTypeface(mTf);
        tvMenu.setText(String.valueOf((char)0xe6f3));
        tvMenuText.setText(R.string.btn_settings);
        tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, tvMenu);
                popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO
                        int id = item.getItemId();
                        switch (id){
                            case R.id.action_logout:
                                doLogout();
                                break;
                            case R.id.action_settings:
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, new SettingsFragment())
                                        .addToBackStack("Settings").commit();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        tvMenuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, tvMenu);
                popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO
                        int id = item.getItemId();
                        switch (id){
                            case R.id.action_settings:
                                fragmentManager.beginTransaction().replace(R.id.container, new SettingsFragment())
                                        .addToBackStack("Settings").commit();
                                break;
                            case R.id.action_logout:
                                doLogout();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });


        fragmentManager.beginTransaction()
                .replace(R.id.container, MapFragment.newInstance())
                .addToBackStack("realTime").commit();
    }

    private void doLogout(){
        mApplication.setIsSignedIn(false);
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    // retrieve access token from preferences
    public boolean isUserSignedIn() {
        boolean hasUserData =
                (
                    (!StringTools.isBlank(mApplication.getAccountID())) &&
                    (!StringTools.isBlank(mApplication.getUserID())) &&
                    (!StringTools.isBlank(mApplication.getPassword()))
                ) || (!StringTools.isBlank(mApplication.getToken()));
        long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
        long expireOn = mApplication.getExpireOn();

        Log.i(TAG, "Current Time: " + currentTime);
        Log.i(TAG, "Expired On: " + expireOn);

        boolean isExpired = (expireOn <= currentTime);

        return !isExpired && hasUserData && mApplication.isSignedIn();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mRequestGetGroup.exec();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private JSONObject createParams(){
        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<>();
            fields.add("accountID");
            fields.add("groupID");
            fields.add("description");
            fields.add("pushpinID");
            fields.add("displayName");
            fields.add("deviceCount");
            fields.add("devicesList");
        List<String> dFields = new ArrayList<String>();
            dFields.add("deviceID");
            dFields.add("description");
            dFields.add("displayName");
            dFields.add("pushpinID");
            dFields.add("isActive");
            dFields.add("lastBatteryLevel");
            dFields.add("lastEventTimestamp");
        JSONObject jsonRequest = null;
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
            jsonParamsObject.put(StringTools.KEY_DEV_FIELDS, new JSONArray(dFields));
            /*jsonRequest = StringTools.createRequest(
                    mApplication.getAccountID(), mApplication.getUserID(), mApplication.getPassword(),
                    StringTools.CMD_GET_GROUPS,
                    Locale.getDefault().getLanguage(),
                    jsonParamsObject
            );*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return /*jsonRequest*/jsonParamsObject;
    }
    @Override
    public void onTrackInfoWindowButton(String deviceId, String desc, long from, long to) {
        Log.i(TAG, "onTrackInfoWindowButton");
        //forBackState.push("ForBackState");
        mApplication.setIsFleet(false);
        mApplication.setSelDevice(deviceId);
        //restoreActionBar(false, desc);
        MapFragment mapFragment = null;
        try {
            mapFragment = (MapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        } catch (ClassCastException e){
            Log.e(TAG, "cannot cast RealTimeFragment");
        }

        if (mapFragment !=null){
            try {
                mapFragment.startHistoricalTracking(deviceId, from, to);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        }
    }
}

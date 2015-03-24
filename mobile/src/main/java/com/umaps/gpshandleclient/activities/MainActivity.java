package com.umaps.gpshandleclient.activities;

import android.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.cluster.TrackInfoWindowAdapter;
import com.umaps.gpshandleclient.fragment.GroupListFragment;
import com.umaps.gpshandleclient.reports.MultiChartsReporting;
import com.umaps.gpshandleclient.reports.SummaryPieChart;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.fragment.MapMonitoringFragment;
import com.umaps.gpshandleclient.fragment.NavigationDrawerFragment;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.util.HTTPRequestQueue;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GroupListFragment.GroupListCallback,
        TrackInfoWindowAdapter.TrackInfoWindowCallback
{
    private static final String TAG = "MainActivity";
    String mTitle;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private JSONObject createJSONRequest(){
        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<String>();
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
            dFields.add("lastEventTimestamp");
        JSONObject jsonRequest = null;
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
            jsonParamsObject.put(StringTools.KEY_DEV_FIELDS, new JSONArray(dFields));
            jsonRequest = StringTools.createRequest(
                    StringTools.CMD_GET_GROUPS,
                    Locale.getDefault().getLanguage(),
                    jsonParamsObject
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }
    private JsonObjectRequest createGetGroupsRequest(){
        JSONObject jsonRequest = createJSONRequest();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getAdministrationUrl(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utilities.HideProgress();
                        SessionState.setGroupList(getApplicationContext(), response);
//                        PrivateStore.
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        return jsonObjectRequest;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        JsonObjectRequest jsonObjectRequest = createGetGroupsRequest();
        HTTPRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

//    @Override
//    protected void onResume(){
//        //-- getGroup and save to SessionState
//        super.onResume();
//        JsonObjectRequest jsonObjectRequest = createGetGroupsRequest();
//        HTTPRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
//    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position+1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position){
            case 0:
                //--set Title for Action
                SessionState.setIsFleet(true);
                restoreActionBar(true, SessionState.getSelGroupDesc());
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MapMonitoringFragment.newInstance())
                        .addToBackStack("realTime").commit();

                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MultiChartsReporting.newInstance())
                        .addToBackStack("multi-charts").commit();
                /*fragmentManager.beginTransaction()
                            .replace(R.id.container, SummaryPieChart.newInstance())
                            .addToBackStack("report").commit();*/
                break;
//            case 2:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, AdministrationFragment.newInstance())
//                        .addToBackStack("administration").commit();
//                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_monitor);
                break;
            case 2:
                mTitle = getString(R.string.title_report);
                break;
            case 3:
                mTitle = getString(R.string.title_administration);
                break;
        }
    }

    public void restoreActionBar(boolean isFleet, String desc) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        if (StringTools.isBlank(desc)){
            desc = getString(R.string.select_a_group);
        }
        if (isFleet) {
            actionBar.setSubtitle(getString(R.string.subtitle_group) + desc);
        } else {
            actionBar.setSubtitle(getString(R.string.subtitle_device) + desc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem actionItem = menu.findItem(R.id.action_list);
            actionItem.setTitle(R.string.action_list_groups);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //--TODO
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .addToBackStack("realTime").commit();*/
            return true;
        }
        if (id == R.id.action_logout){
            Utilities.storeSettings(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_exit){
            Utilities.storeSettings(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGroupItemSelected(String groupId, String desc) {
        Log.i(TAG, "onGroupItemSelected" + groupId);
        SessionState.setSelGroup(groupId);
        SessionState.setSelGroupDesc(desc);
        restoreActionBar(true, desc);
        Fragment fg = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fg == null) return;
        if (fg instanceof MapMonitoringFragment){
            ((MapMonitoringFragment)fg).startRealTimeTracking();
        } else if (fg instanceof SummaryPieChart){
            try {
                ((SummaryPieChart)fg).getSummaryData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (fg instanceof MultiChartsReporting){
            try {
                ((MultiChartsReporting)fg).getSummaryData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Update RealTimeFragment here
//        MapMonitoringFragment mapMonitoringFragment = null;
//        try {
//            mapMonitoringFragment = (MapMonitoringFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.container);
//        } catch (ClassCastException e){
//            Log.e(TAG, "cannot cast RealTimeFragment");
//        }
//
//        if (mapMonitoringFragment !=null){
//            mapMonitoringFragment.startRealTimeTracking();
//        } else {
//            Log.e(TAG, "___onGroupItemSelected");
//        }
    }

    @Override
    public void onChildItemSelected(String deviceId, String desc) {
        Log.i(TAG, "onChildItemSelected" + deviceId);
        SessionState.setSelDevice(deviceId);
        restoreActionBar(false, desc);

        MapMonitoringFragment mapMonitoringFragment = null;
        try {
            mapMonitoringFragment = (MapMonitoringFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        } catch (ClassCastException e){
            Log.e(TAG, "cannot cast RealTimeFragment");
        }
        long to = Calendar.getInstance().getTimeInMillis()/1000;
        long from = to - 60 * 60;   //1 hour
        if (mapMonitoringFragment !=null){
            try {
                mapMonitoringFragment.startHistoricalTracking(deviceId, from, to);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "___onGroupItemSelected");
        }
    }

    private Stack<String> forBackState = new Stack<>();
    @Override
    public void onBackPressed(){
        Log.i(TAG, "onBackPressed");
        if (forBackState.empty()){
            super.onBackPressed();
            return;
        }
        forBackState.pop();
        if (getSupportFragmentManager()
                .findFragmentById(R.id.container) instanceof MapMonitoringFragment){
            MapMonitoringFragment mapMonitoringFragment = (MapMonitoringFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            mapMonitoringFragment.startRealTimeTracking();
            return;
        }
    }

    @Override
    public void onTrackInfoWindowButton(String deviceId, String desc, long from, long to) {
        Log.i(TAG, "onTrackInfoWindowButton");
        forBackState.push("ForBackState");
        SessionState.setIsFleet(false);
        SessionState.setSelDevice(deviceId);
        restoreActionBar(false, desc);
        MapMonitoringFragment mapMonitoringFragment = null;
        try {
            mapMonitoringFragment = (MapMonitoringFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        } catch (ClassCastException e){
            Log.e(TAG, "cannot cast RealTimeFragment");
        }

        if (mapMonitoringFragment !=null){
            try {
                mapMonitoringFragment.startHistoricalTracking(deviceId, from, to);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        }
    }
}

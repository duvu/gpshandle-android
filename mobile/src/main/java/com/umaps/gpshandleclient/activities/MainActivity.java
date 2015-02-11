package com.umaps.gpshandleclient.activities;

import android.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.cluster.TrackInfoWindowAdapter;
import com.umaps.gpshandleclient.fragment.DeviceListDialogFragment;
import com.umaps.gpshandleclient.fragment.GroupListFragment;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.fragment.AdministrationFragment;
import com.umaps.gpshandleclient.fragment.RealTimeFragment;
import com.umaps.gpshandleclient.fragment.NavigationDrawerFragment;
import com.umaps.gpshandleclient.fragment.ReportingFragment;

import org.json.JSONException;


public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GroupListFragment.GroupListCallback, DeviceListDialogFragment.DeviceListCallback,
//        RealTimeFragment.HistoricalCallback,
        TrackInfoWindowAdapter.TrackInfoWindowCallback
{
    private static final String TAG = "MainActivity";
    String mTitle;
    private NavigationDrawerFragment mNavigationDrawerFragment;
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
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position+1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position){
            case 0:
                //--set Title for Action
                SessionState.setIsFleet(true);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RealTimeFragment.newInstance())
                        .addToBackStack("realTime").commit();

                break;
            case 1:
            fragmentManager.beginTransaction()
                        .replace(R.id.container, ReportingFragment.newInstance())
                        .addToBackStack("report").commit();
            break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AdministrationFragment.newInstance())
                        .addToBackStack("administration").commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_monitor);
                SessionState.setSubTitle(getString(R.string.subtitle_real_time));
                break;
            case 2:
                mTitle = getString(R.string.title_historical);
                SessionState.setSubTitle(getString(R.string.subtitle_historical));
                break;
            case 3:
                mTitle = getString(R.string.title_reporting);
                break;
            case 4:
                mTitle = getString(R.string.title_administration);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        if (SessionState.isIsFleet()) {
            actionBar.setSubtitle(SessionState.getSubTitle()
                    + ((SessionState.getSelGroup() != null) ? SessionState.getSelGroup() : "All"));
        } else {
            actionBar.setSubtitle(SessionState.getSubTitle()
                    + ((SessionState.getSelDevice() != null) ? SessionState.getSelDevice() : ""));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem actionItem = menu.findItem(R.id.action_list);
            if (SessionState.isIsFleet()) {
                actionItem.setTitle(R.string.action_list_groups);
            } else if (!SessionState.isIsFleet()){
                actionItem.setTitle(R.string.action_list_devices);
            }
//            MenuItem settingItem = menu.findItem(R.id.action_settings);

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGroupItemSelected(String groupId) {
        Log.i("ABC", "onGroupItemSelected" + groupId);
        SessionState.setSelGroup(groupId);
        restoreActionBar();
        //Update RealTimeFragment here
        RealTimeFragment realTimeFragment = null;
        try {
            realTimeFragment = (RealTimeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        } catch (ClassCastException e){
            Log.e("ABC", "cannot cast RealTimeFragment");
        }

        if (realTimeFragment!=null){
            realTimeFragment.startRealTimeTracking();
        } else {
            Log.e("ABC", "___onGroupItemSelected");
        }
    }

    @Override
    public void onDeviceItemSelected(String deviceID) {
        Log.i("ABC", "onDeviceItemSelected" + deviceID);
//        SessionState.setSelDevice(deviceID);
//        restoreActionBar();
//        //Update RealTimeFragment here
//        HistoricalFragment historicalFragment = (HistoricalFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.container);
//
//        if (historicalFragment!=null){
//            try {
//                historicalFragment.startTracking();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.e("ABC", "___onDeviceItemSelected");
//        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onTrackInfoWindowButton(String deviceId, long from, long to) {
        Log.i(TAG, "onTrackInfoWindowButton");
        restoreActionBar();
        //Update RealTimeFragment here
        SessionState.setIsFleet(false);
        RealTimeFragment realTimeFragment = null;
        try {
            realTimeFragment = (RealTimeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        } catch (ClassCastException e){
            Log.e(TAG, "cannot cast RealTimeFragment");
        }

        if (realTimeFragment!=null){
            try {
                realTimeFragment.startHistoricalTracking(deviceId, from, to);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        }
    }
}

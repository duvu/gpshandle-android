package com.umaps.gpshandleclient.activities;

import android.app.ActionBar;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.maps.SupportMapFragment;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.fragment.AdministrationFragment;
import com.umaps.gpshandleclient.fragment.RealTimeFragment;
import com.umaps.gpshandleclient.fragment.NavigationDrawerFragment;
import com.umaps.gpshandleclient.fragment.ReportingFragment;


public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    String mTitle;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionState.setProgressDialog(showProgressDialog(R.string.application_loading));

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //----------------------------------------------------------------------------------------//
        //-- SETUP APPLICATION-SETTINGS FOR DEBUGGING
        ApplicationSettings.setsAccount("umaps");
        ApplicationSettings.setsUser("admin");
        ApplicationSettings.setsPassword("qwerty123");
        ApplicationSettings.setLocale("en");
        ApplicationSettings.setServerURL("http://dev.gpshandle.com/ws");
        //----------------------------------------------------------------------------------------//
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position+1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position){
            case 0:
                //--set Title for Action
                ApplicationSettings.setIsFleet(true);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RealTimeFragment.newInstance()).commit();
                break;
            case 1:
                ApplicationSettings.setIsFleet(false);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SupportMapFragment.newInstance()).commit();
                break;
            case 2:
            fragmentManager.beginTransaction()
                        .replace(R.id.container, ReportingFragment.newInstance()).commit();
            break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AdministrationFragment.newInstance()).commit();
                break;
        }
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_real_time);
                break;
            case 2:
                mTitle = getString(R.string.title_historical);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem actionItem = menu.findItem(R.id.action_list);
            if (ApplicationSettings.isIsFleet()) {
                actionItem.setTitle(R.string.action_list_groups);
            } else if (!ApplicationSettings.isIsFleet()){
                actionItem.setTitle(R.string.action_list_devices);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}

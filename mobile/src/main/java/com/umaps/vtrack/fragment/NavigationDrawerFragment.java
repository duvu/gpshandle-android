package com.umaps.vtrack.fragment;


import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.umaps.vtrack.R;
import com.umaps.vtrack.activities.BaseActivity;
import com.umaps.vtrack.model.Device;
import com.umaps.vtrack.model.Group;
import com.umaps.vtrack.settings.SessionState;
import com.umaps.vtrack.util.Constants;
import com.umaps.vtrack.util.GpsHandleHTTPAsyncImpl;
import com.umaps.vtrack.util.HTTPDelegateInterface;
import com.umaps.vtrack.views.GroupExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends BaseFragment implements HTTPDelegateInterface {
    private static final String TAG = "NavigationDrawerFragment";
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate( R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_real_time),
                        getString(R.string.title_historical),
                        getString(R.string.title_reporting),
                        getString(R.string.title_administration),
                }));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        GpsHandleHTTPAsyncImpl httpAsync = new GpsHandleHTTPAsyncImpl(NavigationDrawerFragment.this);
        if (item.getItemId() == R.id.action_list) {
            SessionState.getProgressDialog().show();
            //TODO: show list of groups or devices when press on this button
            if (SessionState.isIsFleet()){
                try {
                    httpAsync.getGroups(getActivity().getApplicationContext(),
                            SessionState.getAccountID(),
                            SessionState.getUserID(),
                            SessionState.getPassword(),
                            Locale.getDefault().getLanguage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (!SessionState.isIsFleet()){
                try {
                    httpAsync.getDevices(getActivity().getApplicationContext(),
                            SessionState.getAccountID(),
                            SessionState.getUserID(),
                            SessionState.getPassword(),
                            Locale.getDefault().getLanguage(),
                            SessionState.getGroupID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    @Override
    public void onFinish(JSONObject output) {
        if (SessionState.isIsFleet()){
            //-- Draw list of Groups
            showListViewOfGroup(output);
        } else if (!SessionState.isIsFleet()){
            //-- Draw list of Devices
        }
    }

    private void showListViewOfGroup(JSONObject dataGroups){
        SessionState.getProgressDialog().dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = (View)getActivity().getLayoutInflater().inflate(R.layout.gpshandle_group_expandable_list_view, null);
        builder.setView(view);

        TextView textView = (TextView) view.findViewById(R.id.lv_title);
        textView.setText(R.string.device_list);

        builder.setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //--TODO: implement onclick listener
                return;
            }
        });
        final AlertDialog dialog = builder.create();
        //--Init expandable-list-view here
        ExpandableListView expandableGroupList = (ExpandableListView) view.findViewById(R.id.expandable_group_list);
        {
            //getDisplayWidth

            /*SetIndicator right*/
            DisplayMetrics metrics = new DisplayMetrics();
            int widthPixels = metrics.widthPixels;

            expandableGroupList.setIndicatorBounds(
                    widthPixels - ((BaseActivity)getActivity()).getDisplayFromPixel(50),
                    widthPixels);
        }
        expandableGroupList.setClickable(true);
        //--crete GroupExpandableListAdapter
        ArrayList<Group> groupsList = null;
        HashMap<String, ArrayList< Device >> devicesInGroup = null;
        try {
            groupsList = createGroupList(dataGroups);
            devicesInGroup = createDevicesInGroup(dataGroups);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final GroupExpandableListAdapter groupListAdapter =
                new GroupExpandableListAdapter(getActivity().getApplicationContext(), groupsList, devicesInGroup);
        expandableGroupList.setAdapter(groupListAdapter);

        //--setOnclickListener
        expandableGroupList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Group group = (Group)groupListAdapter.getGroup(groupPosition);
                Log.i(TAG, "OnGroupClicked!+"+group.getGroupId());
                SessionState.setSelGroup(group);
                dialog.dismiss();
                return true;
            }
        });
        //-- Add ExpandableListView to Dialog
        dialog.show();
        return;
    }
    /**
     * Private functions for preparing data for listView*/
    private ArrayList<Group> createGroupList(JSONObject dataGroup) throws JSONException {
        if (dataGroup==null) return null;
        ArrayList<Group> groupsList = new ArrayList<>();
        JSONArray dataGroupArray = dataGroup.getJSONArray(Constants.KEY_RESULT);
        Log.i(TAG, dataGroup.toString());
        for (int i = 0; i<dataGroupArray.length(); i++) {
            JSONObject jsonGroup = dataGroupArray.getJSONObject(i);
            String accountID        = jsonGroup.getString("accountID");
            String groupID          = jsonGroup.getString("groupID");
            String pushpinID        = jsonGroup.getString("pushpinID");
            String groupDescription = jsonGroup.getString("description");
            String groupDisplay     = jsonGroup.getString("displayName");
            int deviceCount         = jsonGroup.getInt("deviceCount");

            //--for check if device is online
            long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
            int countLive = 0;
            JSONArray deviceList = null;
            if (deviceCount>0) {
                deviceList = jsonGroup.getJSONArray("devicesList");
            }
            if (deviceList == null){
                continue;
            }
            for (int j = 0; j<deviceList.length(); j++){
                JSONObject jsonDevice   = deviceList.getJSONObject(j);
                boolean isActive        = jsonDevice.getBoolean("isActive");
                long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");
                if(isActive && (currentTimestamp - lastEventTimestamp < 300)){
                    countLive++;
                }
            }
            Group group = new Group(groupID, (groupDisplay==null?groupDescription:groupDisplay), pushpinID/*icon*/, countLive/*live*/, deviceCount);
            groupsList.add(group);
        }
        return groupsList;
    }
    private HashMap<String, ArrayList<Device>> createDevicesInGroup(JSONObject dataGroup) throws JSONException {
        if (dataGroup==null) return null;
        HashMap<String, ArrayList<Device>> hashMapGroupDevices = new HashMap<>();
        JSONArray dataGroupArray = dataGroup.getJSONArray(Constants.KEY_RESULT);
        for (int i = 0; i<dataGroupArray.length(); i++) {
            //--Reset device-list
            ArrayList<Device> devicesList = new ArrayList<>();
            JSONObject jsonGroup = dataGroupArray.getJSONObject(i);
            String groupID          = jsonGroup.getString("groupID");
            JSONArray jsonArrayDevices = jsonGroup.getJSONArray("devicesList");
            //--for check if device is online
            long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
            for (int j = 0; j<jsonArrayDevices.length(); j++){
                JSONObject jsonDevice = jsonArrayDevices.getJSONObject(j);
                boolean isActive        = jsonDevice.getBoolean("isActive");
                String icon             = jsonDevice.getString("pushpinID");
                String deviceID         = jsonDevice.getString("deviceID");
                String description      = jsonDevice.getString("description");
                long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");
                boolean isLive          = (isActive && (currentTimestamp-lastEventTimestamp<300));
                //--Create new Device Object Model
                Device device = new Device(deviceID, description, icon, isLive, lastEventTimestamp);
                devicesList.add(device);
            }
            hashMapGroupDevices.put(groupID, devicesList);
        }
        return hashMapGroupDevices;
    }
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}

package com.umaps.gpshandleclient.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.util.HTTPRequestQueue;
import com.umaps.gpshandleclient.views.GroupExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class GroupListFragment extends DialogFragment{
    private static final String TAG = "GroupListFragment";
    private GroupListCallback mCallback;

    ExpandableListView expandableGroupList;
    JsonObjectRequest jsonObjectRequest;
    JSONObject jsonRequest;

    private Dialog dialog;

    private JsonObjectRequest createGetGroupsRequest(JSONObject jsonRequest){
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getAdministrationUrl(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utilities.HideProgress();
                        showListViewOfGroup(response);
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
        try {
            jsonParamsObject.put(Utilities.KEY_FIELDS, new JSONArray(fields));
            jsonParamsObject.put(Utilities.KEY_DEV_FIELDS, new JSONArray(dFields));
            jsonRequest = Utilities.createRequest(
                    Utilities.CMD_GET_GROUPS,
                    Locale.getDefault().getLanguage(),
                    jsonParamsObject
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

//    private View exView;
    public static GroupListFragment newInstance(int title) {
        GroupListFragment dialogFragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (GroupListCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        jsonRequest = createJSONRequest();
        createGetGroupsRequest(jsonRequest);

        View v = getActivity().getLayoutInflater().inflate(R.layout.groups_expandable_dialog, null);
        expandableGroupList = (ExpandableListView) v.findViewById(R.id.expandable_group_list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setTitle(R.string.device_list);
        dialog =  builder.create();

        HTTPRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        Utilities.ShowProgress(getActivity(), "", getString(R.string.application_loading));
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = 0;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        return dialog;
    }

    private void showListViewOfGroup(JSONObject dataGroups){
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
        groupListAdapter.setExpandableListView(expandableGroupList);
        expandableGroupList.setAdapter(groupListAdapter);


        //--setOnclickListener
        expandableGroupList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Group group = (Group)groupListAdapter.getGroup(groupPosition);
                Log.i("ABC", "OnGroupClicked!+" + group.getGroupId());
                mCallback.onGroupItemSelected(group.getGroupId());
                getDialog().dismiss();
                return true;
            }
        });
        expandableGroupList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i(TAG, "onChildClick");
                //-- move to this devices
                getDialog().dismiss();
                return true;
            }
        });
    }
    /**
     * Private functions for preparing data for listView*/
    private ArrayList<Group> createGroupList(JSONObject dataGroup) throws JSONException {
        if (dataGroup==null) return null;
        ArrayList<Group> groupsList = new ArrayList<>();
        JSONArray dataGroupArray = dataGroup.getJSONArray(Utilities.KEY_RESULTS);
//        Log.i(TAG, dataGroup.toString());
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
        JSONArray dataGroupArray = dataGroup.getJSONArray(Utilities.KEY_RESULTS);
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

    public interface GroupListCallback{
        void onGroupItemSelected(String groupId);
    }
}

package com.umaps.gpshandleclient.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.util.HTTPRequestQueue;
import com.umaps.gpshandleclient.views.DeviceListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class DeviceListDialogFragment extends DialogFragment{
    private DeviceListCallback mCallback;

    ListView deviceListView;
    JsonObjectRequest jsonObjectRequest;
    JSONObject jsonRequest;

    private JsonObjectRequest createGetDevicesRequest(JSONObject jsonRequest){
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getAdministrationUrl(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            //Update view
                            showListViewOfDevices(response);
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
        fields.add("deviceID");
        fields.add("description");
        fields.add("pushpinID");
        fields.add("displayName");
        fields.add("isActive");
        fields.add("lastEventTimestamp");
        try {
            jsonParamsObject.put(Utilities.FLD_groupID, "all"); //TODO later
            jsonParamsObject.put(Utilities.KEY_FIELDS, new JSONArray(fields));
            jsonRequest = Utilities.createRequest(
                    Utilities.CMD_GET_DEVICES,
                    Locale.getDefault().getLanguage(),
                    jsonParamsObject
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

//    private View exView;
    public static DeviceListDialogFragment newInstance(int title) {
        DeviceListDialogFragment dialogFragment = new DeviceListDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (DeviceListCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        jsonRequest = createJSONRequest();
        createGetDevicesRequest(jsonRequest);
        HTTPRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
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
        View v = getActivity().getLayoutInflater().inflate(R.layout.devices_listview_dialog, null);
        deviceListView = (ListView) v.findViewById(R.id.list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
            .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setTitle(R.string.device_list);
        return builder.create();
    }

    private void showListViewOfDevices(JSONObject data){
        Log.i("#ABC", data.toString());
        deviceListView.setClickable(true);
        ArrayList< Device > devicesList = null;
        try {
            devicesList = createDevicesList(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final DeviceListViewAdapter deviceListAdapter = new DeviceListViewAdapter(getActivity(),devicesList);
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = (Device) parent.getItemAtPosition(position);
                Log.i("DEVICE", "clicked!" + device.getDeviceID());
                getDialog().dismiss();
                //--show calendar dialog
                mCallback.onDeviceItemSelected(device.getDeviceID());
            }
        });
        Utilities.HideProgress();
    }

    /**
     * Private functions for preparing data for listView*/
    private ArrayList<Device> createDevicesList(JSONObject deviceArray) throws JSONException {
        if (deviceArray==null) return null;
        JSONArray jsonDeviceArray = deviceArray.getJSONArray(Utilities.KEY_RESULTS);
        ArrayList<Device> devicesList = new ArrayList<>();
        for (int i = 0; i<jsonDeviceArray.length(); i++) {
            //--Reset device-list
            JSONObject jsonDevice   = jsonDeviceArray.getJSONObject(i);
            String icon             = jsonDevice.getString("pushpinID");
            String deviceID         = jsonDevice.getString("deviceID");
            String description      = jsonDevice.getString("description");
            long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");
            boolean isActive        = jsonDevice.getBoolean("isActive");
            long currentTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
            boolean isLive          = (isActive && (currentTimestamp-lastEventTimestamp<300));
            Log.i("DEVICE#", deviceID);
            //--Create new Device Object Model
            Device device = new Device(deviceID, description, icon, isLive, lastEventTimestamp);
            devicesList.add(device);
        }
        return devicesList;
    }

    public interface DeviceListCallback {
        void onDeviceItemSelected(String deviceID);
    }
}

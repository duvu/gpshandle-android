package com.umaps.gpshandleclient.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.cluster.TrackClusterManager;
import com.umaps.gpshandleclient.cluster.TrackInfoWindowAdapter;
import com.umaps.gpshandleclient.cluster.TrackClusterRenderer;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.model.MapData;
import com.umaps.gpshandleclient.model.MapPoint;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.model.TrackItem;
import com.umaps.gpshandleclient.util.GpsOldRequest;
import com.umaps.gpshandleclient.util.HttpQueue;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpshandleclient.view.CustomMapLayout;
import com.umaps.gpshandleclient.view.DeviceGroupListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG    = "MapMonitoringFragment";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mapFragment;
    private ClusterManager<TrackItem> mClusterManager;

    TrackInfoWindowAdapter trackInfoWindowAdapter;
    TimerTask doAsynchronous = null;
    final Handler handler = new Handler();
    final Timer timer = new Timer();

    private MyApplication mApplication;
    private GpsOldRequest mRequest;
    private View view;

    public static final MapFragment newInstance(){
        return new MapFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mApplication = MyApplication.getInstance();
        Typeface mTf = MyApplication.getIconFont();
        view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        //view.findViewById(R.id.dev_list).setVisibility(View.GONE);

        TextView icRefresh = (TextView) view.findViewById(R.id.ic_refresh);
        TextView txtRefresh = (TextView) view.findViewById(R.id.txt_refresh);
        icRefresh.setTypeface(mTf);
        icRefresh.setText(String.valueOf((char) 0xe607));
        txtRefresh.setText(R.string.btn_refresh);

        TextView icHistory = (TextView) view.findViewById(R.id.ic_history);
        TextView txtHistory = (TextView) view.findViewById(R.id.txt_history);
        icHistory.setTypeface(mTf);
        icHistory.setText(String.valueOf((char) 0xe613));
        txtHistory.setText(R.string.btn_history);

        TextView txtDeviceGroup = (TextView) view.findViewById(R.id.txt_device_group);
        TextView tvDeviceGroupSearch = (TextView) view.findViewById(R.id.ic_device_group_search);
        tvDeviceGroupSearch.setTypeface(mTf);
        tvDeviceGroupSearch.setText(String.valueOf((char) 0xe629));
        txtDeviceGroup.setText(R.string.device_list);
        txtDeviceGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Onclick");
                toggleDeviceList(/*view*/);
            }
        });
        tvDeviceGroupSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDeviceList(/*view*/);
            }
        });

        mRequest = new GpsOldRequest(getActivity());
        mRequest.setAccountID(mApplication.getAccountID());
        mRequest.setUserID(mApplication.getUserID());
        mRequest.setPassword(mApplication.getPassword());
        mRequest.setCommand(GpsOldRequest.CMD_GET_MAP_FLEET);
        mRequest.setMethod(Request.Method.POST);
        mRequest.setUrl(GpsOldRequest.MAPPING_URL);

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment(){
                @Override
                public void onActivityCreated(Bundle savedInstanceState){
                    super.onActivityCreated(savedInstanceState);
                    mMap = mapFragment.getMap();
                }
            };
            getChildFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
            mapFragment.getMapAsync(this);

        } else {
            mMap = mapFragment.getMap();
            mapFragment.getMapAsync(this);
        }
        return view;
    }


    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        if(doAsynchronous!=null){
            doAsynchronous.cancel();
        }
    }

    PolylineOptions rectLine;
    private float currentZoom = 12;
    private void setUpMap() {
        ((CustomMapLayout)view).init(mMap, getPixelsFromDp(getActivity(), 39));
        if (trackInfoWindowAdapter==null){
            trackInfoWindowAdapter = new TrackInfoWindowAdapter(getActivity(), ((CustomMapLayout)view));
        }
        mMap.setInfoWindowAdapter(trackInfoWindowAdapter);
        //-- Setup UI
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
    /**
     * function: realTimeTracking
     * return: void
     **/
    public void realTimeTracking(){
        mMap.clear();
        String groupId = mApplication.getSelGroup();
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(StringTools.FLD_status, null);
            jsonParams.put(StringTools.FLD_inclZones, false);
            jsonParams.put(StringTools.FLD_inclDebug, false);
            jsonParams.put(StringTools.FLD_inclPOI, false);
            jsonParams.put(StringTools.FLD_inclTime, false);
            jsonParams.put(StringTools.FLD_groupID, groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequest.setParams(jsonParams);

        Response.Listener<JSONObject> responseHandler = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                MyResponse mResponse = new MyResponse(response);
                if (mResponse.isError()){
                    Toast.makeText(getActivity(), mResponse.getMessage(), Toast.LENGTH_LONG);
                    return;
                }

                JSONObject jsonObject = (JSONObject)mResponse.getData();
                MapData mapData = new MapData(jsonObject);
                if (mapData == null) {
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLngBounds bounds;
                MapPoint[] pts = mapData.getPoints();
                if (pts.length <= 0) return;
                LatLng lastLatLng = null;
                mClusterManager.clearItems();
                int i = 0;
                for (i = 0; i < pts.length; i++) {
                    LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                    builder.include(latLng);
                    TrackItem to = new TrackItem(pts[i]);
                    mClusterManager.addItem(to);
                }
                float zoom = 0;
                        /*if (isViewing()){
                            //NO-OP
                        } else {*/
                //setViewing(true);
                CameraUpdate cu = null;
                bounds = builder.build();
                LatLng sw = bounds.southwest;
                LatLng ne = bounds.northeast;
                double dt = SphericalUtil.computeDistanceBetween(sw, ne);
                if (dt < 200){
                    cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 14);
                    mMap.moveCamera(cu);
                } else {
                    cu = CameraUpdateFactory.newLatLngBounds(bounds, 100 /*padding*/);
                    mMap.moveCamera(cu);
                }
            }
        };
        Response.ErrorListener errorHandler = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        mRequest.setResponseHandler(responseHandler);
        mRequest.setErrorHandler(errorHandler);
        //Clear old task
        if (doAsynchronous != null) {
            doAsynchronous.cancel();
            doAsynchronous = null;
        }
        doAsynchronous = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRequest.exec();
                    }
                });
            }
        };
        timer.schedule(doAsynchronous, 0, mApplication.getTimeInterval());

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setRenderer(new TrackClusterRenderer(getActivity(), mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterInfoWindowClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterItemClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterItemInfoWindowClickListener(TrackClusterManager.getInstance());
    }

    //-- startHistoricalTracking
    public void startHistoricalTracking(String deviceId, long from, long to) throws JSONException {
        Log.i(TAG, "startHistoricalTracking ...");
        if (doAsynchronous != null) {
            doAsynchronous.cancel();
            doAsynchronous = null;
        }
        JSONObject jsonParamsObject = new JSONObject();
        jsonParamsObject.put(StringTools.FLD_deviceID, deviceId);
        jsonParamsObject.put(StringTools.FLD_status, null);
        jsonParamsObject.put(StringTools.FLD_timeFrom, from/*1421127296*/);
        jsonParamsObject.put(StringTools.FLD_timeTo,   to/*1421137296*/);
        jsonParamsObject.put(StringTools.FLD_inclZones, false);
        jsonParamsObject.put(StringTools.FLD_inclDebug, false);
        jsonParamsObject.put(StringTools.FLD_inclPOI, false);
        jsonParamsObject.put(StringTools.FLD_inclTime, false);
        //get historical data here
        JSONObject jsonRequest = StringTools.createRequest(
                mApplication.getAccountID(), mApplication.getUserID(), mApplication.getPassword(),
                StringTools.CMD_GET_MAP_DEVICE,
                Locale.getDefault().getLanguage(),
                jsonParamsObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                GpsOldRequest.MAPPING_URL,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        JSONObject jsonObject;
                        try {
                            jsonObject = response.getJSONObject(StringTools.KEY_RESULTS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        if (jsonObject == null) {
                            return;
                        }
                        MapData mapData = new MapData(jsonObject);
                        final MapPoint[] pts = mapData.getPoints();
                        if (pts == null||pts.length==0){
                            return;
                        }
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        LatLngBounds bounds;
                        LatLng lastLatLng = null;
                        for (int i = 0; i < pts.length; i++) {
                            LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                            builder.include(latLng);
                            rectLine.add(latLng);
                        }
//                        CameraUpdate cu = null;
//                        bounds = builder.build();
//                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 100 /*padding*/);
//                        mMap.moveCamera(cu);

                        CameraUpdate cu = null;
                        bounds = builder.build();
                        LatLng sw = bounds.southwest;
                        LatLng ne = bounds.northeast;
                        double dt = SphericalUtil.computeDistanceBetween(sw, ne);
                        if (dt < 200){
                            cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 14);
                            mMap.moveCamera(cu);
                        } else {
                            cu = CameraUpdateFactory.newLatLngBounds(bounds, 100 /*padding*/);
                            mMap.moveCamera(cu);
                        }


                        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                if (currentZoom != cameraPosition.zoom) {
                                    currentZoom = cameraPosition.zoom;
                                    mMap.clear();
                                    mMap.addPolyline(rectLine);
                                    float step = (20 - cameraPosition.zoom) * 3;
                                    if (step < 1) step = 1;
                                    for (int i = 0; i < pts.length; i += step) {
                                        LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(""));
                                    }
                                }
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO
                    }
                });
        HttpQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        mMap.clear();
        rectLine = new PolylineOptions().width(3).color(Color.RED);
        mMap.addPolyline(rectLine);

    }
    private void toggleDeviceList(/*View mView*/){
        if (view == null){
            return;
        }
        final View layout = view.findViewById(R.id.dev_list);
        if (layout.getVisibility()==View.VISIBLE){
            layout.setVisibility(View.GONE);
            return;
        } else {
            layout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "toggleDeviceList: " + mApplication.getGroupList());
        MyResponse mRes = new MyResponse(mApplication.getGroupList());
        if (mRes.isError()){
            //TODO update grouplist here
            return;
        }
        List<Group> groupsList = new ArrayList<>();
        HashMap<String, ArrayList< Device >> devicesInGroup = new HashMap<>();
        try {
            JSONArray mJSONArray = (JSONArray) mRes.getData();
            for (int i = 0; i < mJSONArray.length(); i++){
                JSONObject itemGroup = mJSONArray.getJSONObject(i);
                String accountID        = itemGroup.getString("accountID");
                String groupID          = itemGroup.getString("groupID");
                String pushpinID        = itemGroup.getString("pushpinID");
                String groupDescription = itemGroup.getString("description");
                String groupDisplay     = itemGroup.getString("displayName");
                int deviceCount         = itemGroup.getInt("deviceCount");

                long currTimestamp = Calendar.getInstance().getTimeInMillis()/1000;
                int countLive = 0;
                JSONArray deviceList = null;
                if (deviceCount>0) {
                    deviceList = itemGroup.getJSONArray("devicesList");
                }
                if (deviceList == null){
                    continue;
                }

                ArrayList<Device> mArrayDevice = new ArrayList<>();
                for (int j = 0; j<deviceList.length(); j++){
                    JSONObject jsonDevice   = deviceList.getJSONObject(j);
                    boolean isActive        = jsonDevice.getBoolean("isActive");
                    String icon             = jsonDevice.getString("pushpinID");
                    String deviceID         = jsonDevice.getString("deviceID");
                    String description      = jsonDevice.getString("description");
                    Double lastBatteryLevel = jsonDevice.getDouble("lastBatteryLevel");
                    long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");

                    boolean isLive          = (isActive && (currTimestamp-lastEventTimestamp<300));
                    //--for group
                    if(isActive && (currTimestamp - lastEventTimestamp < 300)){
                        countLive++;
                    }

                    //--Create new Device Object Model
                    Device device = new Device(deviceID, description, icon, isLive, lastEventTimestamp);
                    device.setBatteryLevel(lastBatteryLevel);
                    mArrayDevice.add(device);
                }
                Group group = new Group(accountID, groupID, (groupDisplay==null?groupDescription:groupDisplay),
                        (groupDisplay==null?groupDescription:groupDisplay),
                        pushpinID/*icon*/, countLive/*live*/, deviceCount);
                groupsList.add(group);
                devicesInGroup.put(groupID, mArrayDevice);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "toggleDeviceList1");
        ExpandableListView expandableGroupList = (ExpandableListView) view.findViewById(R.id.expandable_group_list);
        expandableGroupList.setClickable(true);
        final DeviceGroupListAdapter groupListAdapter =
                new DeviceGroupListAdapter(getActivity(), groupsList, devicesInGroup);
        if (groupListAdapter != null){
            groupListAdapter.setExpandableListView(expandableGroupList);
            expandableGroupList.setAdapter(groupListAdapter);
        }

        final TextView tvDeviceGroup = (TextView) view.findViewById(R.id.txt_device_group);
        //--setOnclickListener
        expandableGroupList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Group group = (Group) groupListAdapter.getGroup(groupPosition);
                Log.i("ABC", "OnGroupClicked!+" + group.getGroupId());
                mApplication.setSelGroup(group.getGroupId());
                mApplication.setIsFleet(true);
                tvDeviceGroup.setText(mApplication.getSelGroup());
                realTimeTracking();
                layout.setVisibility(View.GONE);
                return true;
            }
        });
        expandableGroupList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i(TAG, "onChildClick");
                //-- Move to latest position of this devices
                Device device = (Device) groupListAdapter.getChild(groupPosition, childPosition);
                mApplication.setSelDevice(device.getDeviceID());
                mApplication.setIsFleet(false);
                tvDeviceGroup.setText(mApplication.getSelDevice());

                long to = Calendar.getInstance().getTimeInMillis() / 1000;
                long from = to - 60 * 60;   //1 hour
                try {
                    startHistoricalTracking(device.getDeviceID(), from, to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                layout.setVisibility(View.GONE);
                return true;
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mClusterManager==null) {
            mClusterManager = new ClusterManager<TrackItem>(getActivity(), mMap);
        } else {
            mClusterManager.clearItems();
        }
        setUpMap();
        realTimeTracking();
    }
}

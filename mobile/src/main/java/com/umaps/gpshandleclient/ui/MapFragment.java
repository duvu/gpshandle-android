package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpshandleclient.view.CustomMapLayout;
import com.umaps.gpshandleclient.util.DeviceGroupListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        TrackInfoWindowAdapter.TrackInfoWindowCallback  {
    private static final String TAG    = "MapMonitoringFragment";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mapFragment;
    private ClusterManager<TrackItem> mClusterManager;

    private static final String TAG_REQUEST = "TagRequest";

    TrackInfoWindowAdapter trackInfoWindowAdapter;
    TimerTask doAsynchronous = null;
    final Handler handler = new Handler();
    final Timer timer = new Timer();

    private MyApplication mApplication;
    private GpsOldRequest mRequestRealtime;
    private GpsOldRequest mRequestHistory;
    private GpsOldRequest mRequestGetGroup;
    private View view;

    private boolean isRunning = false;

    private View mBarProgress;
    private View mProgress;
    private View mView;
    TextView txtDeviceGroup;
    View mHistoryOptions;
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
        view = inflater.inflate(R.layout.frag_monitoring, container, false);
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mView = view.findViewById(R.id.map);
        txtDeviceGroup = (TextView) view.findViewById(R.id.txt_device_group);
        mHistoryOptions = view.findViewById(R.id.options_history);

        TextView tvDeviceGroupSearch = (TextView) view.findViewById(R.id.ic_device_group_search);
        tvDeviceGroupSearch.setTypeface(mTf);
        tvDeviceGroupSearch.setText(String.valueOf((char) 0xe629));
        txtDeviceGroup.setText(mApplication.getSelGroupDesc());

        View deviceGroup = view.findViewById(R.id.device_group);
        deviceGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDeviceList(/*view*/);
            }
        });
        mApplication.setIsFleet(true);


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

        View lGroupAll = view.findViewById(R.id.l_group_all);
        lGroupAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.setSelGroup("all");
                mApplication.setSelGroupDesc(String.valueOf(getText(R.string.txt_group_all)));
                mApplication.setIsFleet(true);
                mApplication.storeSettings();
                realTimeTracking();
            }
        });
        return view;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mBarProgress = null;
        mProgress = null;
        if (mRequestRealtime != null) {
            try {
                mRequestRealtime.cancel(TAG_REQUEST);
            } catch (NullPointerException ne){
                ne.printStackTrace();
                return;
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if ((mMap != null) && (mClusterManager!=null)) {
            realTimeTracking();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if(doAsynchronous!=null){
            doAsynchronous.cancel();
        }
    }

    PolylineOptions historicalData;
    private float currentZoom = 12;
    private void setUpMap() {
        ((CustomMapLayout)view).init(mMap, getPixelsFromDp(getActivity(), 39));
        if (trackInfoWindowAdapter==null){
            trackInfoWindowAdapter = new TrackInfoWindowAdapter(this, ((CustomMapLayout)view));
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
    boolean isShowing = false;
    float zoom = 0;
    CameraUpdate cu = null;
    public void realTimeTracking(){
        isShowing = false;
        String groupId = mApplication.getSelGroup();
        if (StringTools.isBlank(groupId) || "all".equalsIgnoreCase(groupId)){
            txtDeviceGroup.setText(R.string.select_a_group);
        }
        txtDeviceGroup.setText(mApplication.getSelGroupDesc());
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
        mRequestRealtime = new GpsOldRequest(getActivity());
        mRequestRealtime.setAccountID(mApplication.getAccountID());
        mRequestRealtime.setUserID(mApplication.getUserID());
        mRequestRealtime.setPassword(mApplication.getPassword());
        mRequestRealtime.setCommand(GpsOldRequest.CMD_GET_MAP_FLEET);
        mRequestRealtime.setMethod(Request.Method.POST);
        mRequestRealtime.setUrl(GpsOldRequest.MAPPING_URL);
        mRequestRealtime.setParams(jsonParams);
        Response.Listener<JSONObject> responseHandler = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                isRunning = false;
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
                if ((pts == null) || (pts.length <= 0)) return;

                mClusterManager.clearItems();
                for (int i = 0; i < pts.length; i++) {
                    LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                    builder.include(latLng);
                    TrackItem to = new TrackItem(pts[i]);
                    mClusterManager.addItem(to);
                }
                if (!isShowing){
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
                    isShowing = true;
                } else {
                    //-- Noop
                }
            }
        };
        Response.ErrorListener errorHandler = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRunning = false;
            }
        };
        mRequestRealtime.setResponseHandler(responseHandler);
        mRequestRealtime.setErrorHandler(errorHandler);
        mRequestRealtime.setRequestTag(TAG_REQUEST);
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
                        if (!isRunning) {
                            mRequestRealtime.exec();
                            isRunning = true;
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronous, 0, mApplication.getTimeInterval());

        mClusterManager.setRenderer(new TrackClusterRenderer(getActivity(), mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterInfoWindowClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterItemClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterItemInfoWindowClickListener(TrackClusterManager.getInstance());

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.clear();
    }

    //-- startHistoricalTracking
    public void startHistoricalTracking(final String deviceId, long from, long to) {
        txtDeviceGroup.setText(mApplication.getSelDeviceDesc());
        if (doAsynchronous != null) {
            doAsynchronous.cancel();
            doAsynchronous = null;
        }
        JSONObject jsonParamsObject = new JSONObject();
        try {
            jsonParamsObject.put(StringTools.FLD_deviceID, deviceId);
            jsonParamsObject.put(StringTools.FLD_status, null);
            jsonParamsObject.put(StringTools.FLD_timeFrom, from/*1421127296*/);
            jsonParamsObject.put(StringTools.FLD_timeTo,   to/*1421137296*/);
            jsonParamsObject.put(StringTools.FLD_inclZones, false);
            jsonParamsObject.put(StringTools.FLD_inclDebug, false);
            jsonParamsObject.put(StringTools.FLD_inclPOI, false);
            jsonParamsObject.put(StringTools.FLD_inclTime, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequestHistory = new GpsOldRequest(getActivity());
        mRequestHistory.setAccountID(mApplication.getAccountID());
        mRequestHistory.setUserID(mApplication.getUserID());
        mRequestHistory.setPassword(mApplication.getPassword());
        mRequestHistory.setUrl(GpsOldRequest.MAPPING_URL);
        mRequestHistory.setParams(jsonParamsObject);
        mRequestHistory.setCommand(GpsOldRequest.CMD_GET_MAP_DEVICE);
        mRequestHistory.setMethod(Request.Method.POST);

        mRequestHistory.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());

                MyResponse myResponse = new MyResponse(response);
                if (myResponse.isError()) {
                    Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject jsonObject = (JSONObject) myResponse.getData();
                if (jsonObject == null) {
                    return;
                }
                MapData mapData = new MapData(jsonObject);
                if (mapData == null) {
                    return;
                }
                final MapPoint[] pts = mapData.getPoints();
                if (pts == null || pts.length == 0) {
                    Toast.makeText(getActivity(),
                        getString(R.string.no_history_events) + "for device: " + mApplication.getSelDeviceDesc(),
                        Toast.LENGTH_LONG).show();
                    realTimeTracking();
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLngBounds bounds;
                for (int i = 0; i < pts.length; i++) {
                    LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                    builder.include(latLng);
                    historicalData.add(latLng);
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
                if (dt < 200) {
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
                            mMap.addPolyline(historicalData);
                            float step = (20 - cameraPosition.zoom) * 3;
                            if (step < 1) step = 1;


                            for (int i = 0; i < pts.length; i += step) {
                                //if (i >= pts.length) i = pts.length-1;
                                LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                                MarkerOptions flatMarkerOpt = new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                                        .position(latLng)
                                        .flat(true)
                                        .anchor(0.5f, 0.5f)
                                        .rotation((float) pts[i].getHeading());

                                mMap.addMarker(flatMarkerOpt);
                            }
                        }
                    }
                });

            }
        });
        mRequestHistory.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
            }
        });
        mRequestHistory.setRequestTag(TAG_REQUEST);
        mRequestHistory.exec();
        mMap.clear();
        historicalData = new PolylineOptions().width(3).color(Color.RED);
        //mMap.addPolyline(historicalData);
    }

    /**
     * function toggle device list
     **/
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

        mRequestGetGroup = new GpsOldRequest(getActivity());
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
                showProgress(false);
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) {
                    return;
                }
                ArrayList<Group> groupsList = new ArrayList<>();
                HashMap<String, ArrayList<Device>> devicesInGroup = new HashMap<>();
                try {
                    JSONArray mJSONArray = (JSONArray) mRes.getData();
                    for (int i = 0; i < mJSONArray.length(); i++) {
                        JSONObject itemGroup = mJSONArray.getJSONObject(i);
                        String accountID = itemGroup.getString("accountID");
                        String groupID = itemGroup.getString("groupID");
                        String pushpinID = itemGroup.getString("pushpinID");
                        String groupDescription = itemGroup.getString("description");
                        String groupDisplay = itemGroup.getString("displayName");
                        int deviceCount = itemGroup.getInt("deviceCount");

                        long currTimestamp = Calendar.getInstance().getTimeInMillis() / 1000;
                        int countLive = 0;
                        JSONArray deviceList = null;
                        if (deviceCount > 0) {
                            deviceList = itemGroup.getJSONArray("devicesList");
                        }
                        if (deviceList == null) {
                            continue;
                        }

                        ArrayList<Device> mArrayDevice = new ArrayList<>();
                        for (int j = 0; j < deviceList.length(); j++) {
                            JSONObject jsonDevice = deviceList.getJSONObject(j);
                            boolean isActive = jsonDevice.getBoolean("isActive");
                            String icon = jsonDevice.getString("pushpinID");
                            String deviceID = jsonDevice.getString("deviceID");
                            String description = jsonDevice.getString("description");
                            Double lastBatteryLevel = jsonDevice.getDouble("lastBatteryLevel");
                            long lastEventTimestamp = jsonDevice.getLong("lastEventTimestamp");

                            Log.i(TAG, currTimestamp+"/"+lastEventTimestamp);
                            boolean isLive = (isActive && (currTimestamp - lastEventTimestamp < 300));
                            //--for group
                            if (isLive) {
                                countLive++;
                            }

                            //--Create new Device Object Model
                            Device device = new Device(deviceID, description, icon, isLive, lastEventTimestamp);
                            device.setBatteryLevel(lastBatteryLevel);
                            mArrayDevice.add(device);
                        }
                        Group group = new Group(
                                accountID,
                                groupID,
                                (groupDisplay == null ? groupDescription : groupDisplay),
                                (groupDisplay == null ? groupDescription : groupDisplay),
                                pushpinID/*icon*/,
                                countLive/*live*/,
                                deviceCount);
                        groupsList.add(group);
                        devicesInGroup.put(groupID, mArrayDevice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ExpandableListView expGroupList = (ExpandableListView) view.findViewById(R.id.expandable_group_list);
                expGroupList.setClickable(true);
                final DeviceGroupListAdapter groupListAdapter = new DeviceGroupListAdapter(getActivity(), groupsList, devicesInGroup);
                expGroupList.setAdapter(groupListAdapter);
                groupListAdapter.setExpGroupList(expGroupList);

                //--setOnclickListener
                expGroupList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        Group group = (Group) groupListAdapter.getGroup(groupPosition);
                        //Log.i(TAG, "OnGroupClicked!+" + group.getGroupId());
                        mApplication.setSelGroup(group.getGroupId());
                        mApplication.setSelGroupDesc(group.getDescription());
                        mApplication.setIsFleet(true);
                        mApplication.storeSettings();
                        realTimeTracking();
                        layout.setVisibility(View.GONE);
                        return true;
                    }
                });
                expGroupList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        Log.i(TAG, "onChildClick");
                        //-- Move to latest position of this devices
                        Device device = (Device) groupListAdapter.getChild(groupPosition, childPosition);
                        mApplication.setSelDevice(device.getDeviceID());
                        mApplication.setSelDeviceDesc(device.getDescription());
                        mApplication.setIsFleet(false);
                        mApplication.storeSettings();
                        onTrackInfoWindowButton(device.getDeviceID(), device.getDisplayName());
                        layout.setVisibility(View.GONE);
                        return true;
                    }
                });
            }
        });
        mRequestGetGroup.setRequestTag(TAG_REQUEST);
        mRequestGetGroup.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRequestGetGroup.exec();
        showProgress(true);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mView.setVisibility(show ? View.GONE : View.VISIBLE);
            mView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mBarProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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

    @Override
    public void onTrackInfoWindowButton(final String deviceId, String desc) {
        mApplication.setIsFleet(false);
        mApplication.setSelDevice(deviceId);
        txtDeviceGroup.setText(desc);
        if (mHistoryOptions.getVisibility() == View.GONE){
            mHistoryOptions.setVisibility(View.VISIBLE);
        }

        //-- show select time table
        Button btn30min = (Button) view.findViewById(R.id.btn_historical_30m);
        btn30min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "got clicked on btn30min");
                //--load 30 min historical data
                /*if (mHistoryOptions.getVisibility() == View.VISIBLE) {
                    mHistoryOptions.setVisibility(View.GONE);
                }
                long timeTo = Calendar.getInstance().getTimeInMillis() / 1000;
                long timeFrom = timeTo - 30 * 60;
                MapFragment.this.startHistoricalTracking(deviceId, timeFrom, timeTo);
                return;*/
                getHistory(deviceId, 30*60);
            }
        });

        Button btn60min = (Button) view.findViewById(R.id.btn_historical_60m);
        btn60min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "got clicked on btn60min");
                //--load 30 min historical data
                /*if (mHistoryOptions.getVisibility() == View.VISIBLE) {
                    mHistoryOptions.setVisibility(View.GONE);
                }
                long timeTo = Calendar.getInstance().getTimeInMillis() / 1000;
                long timeFrom = timeTo - 60 * 60;
                MapFragment.this.startHistoricalTracking(deviceId, timeFrom, timeTo);
                return;*/
                getHistory(deviceId, 60*60);
            }
        });
        Button btn6h = (Button) view.findViewById(R.id.btn_historical_6h);
        btn6h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "got clicked on btn6h");
                /*if (mHistoryOptions.getVisibility() == View.VISIBLE) {
                    mHistoryOptions.setVisibility(View.GONE);
                }
                long timeTo = Calendar.getInstance().getTimeInMillis() / 1000;
                long timeFrom = timeTo - 6 * 60 * 60;
                MapFragment.this.startHistoricalTracking(deviceId, timeFrom, timeTo);
                return;*/
                getHistory(deviceId, 6*60*60);
            }
        });
        Button btn12h = (Button) view.findViewById(R.id.btn_historical_12h);
        btn12h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "got clicked on btn6h");
                /*if (mHistoryOptions.getVisibility() == View.VISIBLE) {
                    mHistoryOptions.setVisibility(View.GONE);
                }
                long timeTo = Calendar.getInstance().getTimeInMillis() / 1000;
                long timeFrom = timeTo - 12 * 60 * 60;
                MapFragment.this.startHistoricalTracking(deviceId, timeFrom, timeTo);
                return;*/
                getHistory(deviceId, 12*60*60);
            }
        });
    }

    private void getHistory(String deviceId, long duration){
        if (mHistoryOptions.getVisibility() == View.VISIBLE) {
            mHistoryOptions.setVisibility(View.GONE);
        }
        long timeTo = Calendar.getInstance().getTimeInMillis() / 1000;
        long timeFrom = timeTo - duration;
        startHistoricalTracking(deviceId, timeFrom, timeTo);
        return;
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
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
            jsonParamsObject.put(StringTools.KEY_DEV_FIELDS, new JSONArray(dFields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParamsObject;
    }
}

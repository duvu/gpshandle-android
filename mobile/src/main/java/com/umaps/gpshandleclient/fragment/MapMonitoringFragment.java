package com.umaps.gpshandleclient.fragment;

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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.umaps.gpshandleclient.cluster.TrackObjectRenderer;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.model.MapData;
import com.umaps.gpshandleclient.model.TrackObject;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.util.HTTPRequestQueue;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpshandleclient.view.CustomMapLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MapMonitoringFragment extends Fragment {
    private final String TAG    = "MapMonitoringFragment";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mapFragment;
    Typeface mTf = null;

    private ClusterManager<TrackObject> mClusterManager;
    CustomMapLayout customMapLayout;

    TrackInfoWindowAdapter trackInfoWindowAdapter;

    TimerTask doAsynchronous = null;

    private MapData.Point[] pts = null;

    public static final MapMonitoringFragment newInstance(){
        return new MapMonitoringFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        //-- getGroup
        super.onAttach(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring, container, false);

        mTf = Typeface.createFromAsset(getActivity().getAssets(), "icomoon.ttf");
        TextView tvDeviceList = (TextView) view.findViewById(R.id.device_list);
        TextView tvDeviceListText = (TextView) view.findViewById(R.id.device_list_text);
        tvDeviceList.setTypeface(mTf);
        tvDeviceList.setText(String.valueOf((char)0xe64e));
        tvDeviceListText.setText(R.string.device_list);


        setUpMapIfNeeded();
        customMapLayout = (CustomMapLayout) view;
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        startRealTimeTracking();
    }
    @Override
    public void onPause(){
        super.onPause();
        if(doAsynchronous!=null){
            doAsynchronous.cancel();
        }
    }

    /**
     */
    private void setUpMapIfNeeded() {
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment(){
                @Override
                public void onActivityCreated(Bundle savedInstanceState){
                    super.onActivityCreated(savedInstanceState);

                    mMap = mapFragment.getMap();
                }
            };
            getChildFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();

        } else {
            mMap = mapFragment.getMap();
        }

        if (mMap != null) {
            if (mClusterManager==null) {
                mClusterManager = new ClusterManager<TrackObject>(getActivity(), mMap);
            } else {
                mClusterManager.clearItems();
            }
            setUpMap();
        }
    }
    PolylineOptions rectLine;
    private float currentZoom = 12;
    private void setUpMap() {

        customMapLayout.init(mMap, getPixelsFromDp(getActivity(),39));
        if (trackInfoWindowAdapter==null){
            trackInfoWindowAdapter = new TrackInfoWindowAdapter(getActivity(), customMapLayout);
        }
        //-- Setup UI
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);



        //--For historical tracking
        if (!SessionState.isIsFleet()) {
            mMap.clear();
            rectLine = new PolylineOptions().width(3).color(Color.RED);
            mMap.addPolyline(rectLine);
            mMap.setInfoWindowAdapter(trackInfoWindowAdapter);
        }
        //--for Real time tracking
        else {
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mMap.setInfoWindowAdapter(trackInfoWindowAdapter);

            mClusterManager.setRenderer(new TrackObjectRenderer(getActivity(), mMap, mClusterManager));
            mClusterManager.setOnClusterClickListener(TrackClusterManager.getInstance());
            mClusterManager.setOnClusterInfoWindowClickListener(TrackClusterManager.getInstance());
            mClusterManager.setOnClusterItemClickListener(TrackClusterManager.getInstance());
            mClusterManager.setOnClusterItemInfoWindowClickListener(TrackClusterManager.getInstance());
        }


    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
    public void startRealTimeTracking(){
        Log.i(TAG, "startRealTimeTracking ...");
        SessionState.setIsFleet(true);
        setUpMapIfNeeded();
        mMap.clear();
        setViewing(false);
        String groupId = SessionState.getSelGroup();
        if (StringTools.isBlank(groupId)){
            //return;
            groupId="all";
        }
        try {
            getRunInterval(groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    final Handler handler = new Handler();
    final Timer timer = new Timer();
    boolean isViewing = false;
    private void setViewing(boolean first){
        isViewing = first;
    }
    private boolean isViewing(){
        return isViewing;
    }
    private void getRunInterval(String groupId) throws JSONException {
        JSONObject jsonParamsObject = new JSONObject();
        jsonParamsObject.put(StringTools.FLD_groupID, groupId);
        jsonParamsObject.put(StringTools.FLD_status, null);
        jsonParamsObject.put(StringTools.FLD_inclZones, false);
        jsonParamsObject.put(StringTools.FLD_inclDebug, false);
        jsonParamsObject.put(StringTools.FLD_inclPOI, false);
        jsonParamsObject.put(StringTools.FLD_inclTime, false);
        //--manage ACL
        if (SessionState.getAclMapMonitor()<1){
            return;
        }

        JSONObject jsonRequest = StringTools.createRequest(
                StringTools.CMD_GET_MAP_FLEET,
                Locale.getDefault().getLanguage(),
                jsonParamsObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getMappingUrl(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject jsonObject = null;
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
                        if (mapData == null) {
                            return;
                        }
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        LatLngBounds bounds;
                        pts = mapData.getPoints();
                        if (pts.length <= 0) return;
                        LatLng lastLatLng = null;
                        mClusterManager.clearItems();
                        int i = 0;
                        for (i = 0; i < pts.length; i++) {
                            LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                            builder.include(latLng);
                            TrackObject to = null;
                            to = new TrackObject(latLng);
                            to.setPointData(pts[i]);        //Point's Data
                            mClusterManager.addItem(to);
                        }
                        float zoom = 0;
                        if (isViewing()){
                            //NO-OP
                        } else {
                            setViewing(true);
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        doInBackground(jsonObjectRequest);
    }
    private void doInBackground(final JsonObjectRequest jsonObjectRequest){
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
                        HTTPRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
                    }
                });
            }
        };
        timer.schedule(doAsynchronous, 0, ApplicationSettings.getTimeInterval());
    }


    public void startHistoricalTracking(String deviceId, long from, long to) throws JSONException {
        Log.i(TAG, "startHistoricalTracking ...");
        Utilities.ShowProgress(getActivity(), "", getString(R.string.application_loading));
        if (doAsynchronous != null) {
            doAsynchronous.cancel();
            doAsynchronous = null;
        }
        SessionState.setIsFleet(false);
        setUpMapIfNeeded();
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
        JSONObject jsonRequest = StringTools.createRequest(StringTools.CMD_GET_MAP_DEVICE,
                Locale.getDefault().getLanguage(),
                jsonParamsObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getMappingUrl(),
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
                            Utilities.HideProgress();
                            return;
                        }
                        if (jsonObject == null) {
                            Utilities.HideProgress();
                            return;
                        }
                        MapData mapData = new MapData(jsonObject);
                        pts = mapData.getPoints();
                        if (pts == null||pts.length==0){
                            Utilities.HideProgress();
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
                                if (currentZoom != cameraPosition.zoom){
                                    currentZoom = cameraPosition.zoom;
                                    mMap.clear();
                                    mMap.addPolyline(rectLine);
                                    float step = (20 -cameraPosition.zoom)*3;
                                    if (step<1) step = 1;
                                    for (int i = 0; i < pts.length; i+=step) {
                                        LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(""));
                                    }
                                }
                            }
                        });
                        Utilities.HideProgress();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO
                        Utilities.HideProgress();
                    }
                });
        HTTPRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


//    /*Interface for communicate with MainActivity*/
//    public interface HistoricalCallback{
//        public void onWindowInfoHistoricalPressed();
//    }
}

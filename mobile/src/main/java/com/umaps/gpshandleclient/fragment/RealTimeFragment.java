package com.umaps.gpshandleclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.umaps.gpshandleclient.views.CustomMapLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class RealTimeFragment extends Fragment {
    private final String TAG    = "TRACKING";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mapFragment;

    private ClusterManager<TrackObject> mClusterManager;
    CustomMapLayout customMapLayout;

    TrackInfoWindowAdapter trackInfoWindowAdapter;

    TimerTask doAsynchronous = null;

    private MapData.Point[] pts = null;

    public static RealTimeFragment newInstance(){
        return new RealTimeFragment();
    }
    public RealTimeFragment(){
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_real_time, container, false);
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
        setUpMapIfNeeded();
        try {
            getRunInterval();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    final Handler handler = new Handler();
    final Timer timer = new Timer();
    private void getRunInterval() throws JSONException {
        JSONObject jsonParamsObject = new JSONObject();
        jsonParamsObject.put(Utilities.FLD_groupID, SessionState.getSelGroup());
        jsonParamsObject.put(Utilities.FLD_status, null);
        jsonParamsObject.put(Utilities.FLD_inclZones, false);
        jsonParamsObject.put(Utilities.FLD_inclDebug, false);
        jsonParamsObject.put(Utilities.FLD_inclPOI, false);
        jsonParamsObject.put(Utilities.FLD_inclTime, false);
        //--manage ACL
        if (SessionState.getAclMapMonitor()<1){
            return;
        }

        JSONObject jsonRequest = Utilities.createRequest(
                Utilities.CMD_GET_MAP_FLEET,
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
                            jsonObject = response.getJSONObject(Utilities.KEY_RESULTS);
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
                        /*MapData.Point[] */pts = mapData.getPoints();
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
                        CameraUpdate cu = null;
                        bounds = builder.build();
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 100 /*padding*/);
                        mMap.moveCamera(cu);
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
        if (doAsynchronous != null) {
            doAsynchronous.cancel();
            doAsynchronous = null;
        }
        setUpMapIfNeeded();
        JSONObject jsonParamsObject = new JSONObject();
        jsonParamsObject.put(Utilities.FLD_deviceID, deviceId/*SessionState.getSelDevice()*//*"tg102_1204022866"/*SessionState.getSelDevice()*/);
        jsonParamsObject.put(Utilities.FLD_status, null);
        jsonParamsObject.put(Utilities.FLD_timeFrom, from/*1421127296*/);
        jsonParamsObject.put(Utilities.FLD_timeTo,   to/*1421137296*/);
        jsonParamsObject.put(Utilities.FLD_inclZones, false);
        jsonParamsObject.put(Utilities.FLD_inclDebug, false);
        jsonParamsObject.put(Utilities.FLD_inclPOI, false);
        jsonParamsObject.put(Utilities.FLD_inclTime, false);
        //get historical data here
        JSONObject jsonRequest = Utilities.createRequest(Utilities.CMD_GET_MAP_DEVICE,
                Locale.getDefault().getLanguage(),
                jsonParamsObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApplicationSettings.getMappingUrl(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO
                        Log.i("ABC", response.toString());
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = response.getJSONObject(Utilities.KEY_RESULTS);
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
                        pts = mapData.getPoints();
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
                        CameraUpdate cu = null;
                        bounds = builder.build();
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 100 /*padding*/);
                        mMap.moveCamera(cu);


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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO
                    }
                });
        HTTPRequestQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


//    /*Interface for communicate with MainActivity*/
//    public interface HistoricalCallback{
//        public void onWindowInfoHistoricalPressed();
//    }
}

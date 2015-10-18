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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import com.umaps.gpshandleclient.event.UpdateEvent;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.model.MapData;
import com.umaps.gpshandleclient.model.MapPoint;
import com.umaps.gpshandleclient.model.TrackItem;
import com.umaps.gpshandleclient.util.EBus;
import com.umaps.gpshandleclient.view.CustomMapLayout;
import com.umaps.gpshandleclient.view.GenericViewFragment;
import com.umaps.gpssdk.GpsRequest;
import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.MyResponse;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class MapFragment extends GenericViewFragment implements OnMapReadyCallback,
        TrackInfoWindowAdapter.TrackInfoWindowCallback  {
    private static final String TAG    = "MapFragment";
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ClusterManager<TrackItem> mClusterManager;
    private TrackInfoWindowAdapter trackInfoWindowAdapter;
    private TimerTask doAsynchronous = null;
    final Handler handler = new Handler();
    final Timer timer = new Timer();

    private MyApplication mApplication;

    private GpsRequest monitorRequest;
    private GpsRequest historyRequest;
    //private GpsRequest groupRequest;

    private View view;
    private boolean isRunning = false;

    private View mBarProgress;
    private View mProgress;
    private View mView;
    private View mHistoryOptions;

    public static final MapFragment newInstance(){
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_monitoring, container, false);
        GpsSdk.setSessionId(0); //to track page
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mView = view.findViewById(R.id.map);

        mApplication = MyApplication.getInstance();
        Typeface mTf = MyApplication.getIconFont();
        mHistoryOptions = view.findViewById(R.id.options_history);
        mApplication.setIsFleet(true);
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment(){
                @Override
                public void onActivityCreated(Bundle savedInstanceState){
                    super.onActivityCreated(savedInstanceState);
                }
            };
            getChildFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
            mapFragment.getMapAsync(this);

        } else {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        GpsRequest.getInstance().cancelAll();
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
        if (monitorRequest != null) {
            monitorRequest.cancelAll();
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
    public synchronized void realTimeTracking(){

        EventBus.getDefault().post(new UpdateEvent.OnLive(true));
        isShowing = false;
        String groupId = GpsSdk.getSelectedGroup(); //mApplication.getSelGroup();

        monitorRequest = GpsRequest.getFleetRequest(getActivity(), groupId);
        Response.Listener<JSONObject> responseHandler = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                EventBus.getDefault().post(new UpdateEvent.OnLoading(false));
                isRunning = false;
                MyResponse mResponse = new MyResponse(response);
                if (mResponse.isError()){
                    Toast.makeText(getActivity(), mResponse.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject jsonObject = (JSONObject)mResponse.getData();
                MapData mapData = new MapData(jsonObject);
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

        monitorRequest.setResponseHandler(responseHandler);
        monitorRequest.setErrorHandler(errorHandler);

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
                        if (GpsSdk.getSessionId() != 0) {
                            return;
                        }
                        EventBus.getDefault().post(new UpdateEvent.OnLoading(true));
                        if (!isRunning) {
                            monitorRequest.exec();
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
        EventBus.getDefault().post(new UpdateEvent.OnLive(false));
        if (doAsynchronous != null) {
            doAsynchronous.cancel();
            doAsynchronous = null;
        }
        historyRequest = GpsRequest.geMapsRequest(getActivity(), deviceId, from, to);
        historyRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
        historyRequest.exec();
        mMap.clear();
        historicalData = new PolylineOptions().width(3).color(Color.RED);
        //mMap.addPolyline(historicalData);
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
        if (mapFragment != null) {
            mMap = mapFragment.getMap();
        }
        if (mClusterManager==null) {
            mClusterManager = new ClusterManager<TrackItem>(getActivity(), mMap);
        } else {
            mClusterManager.clearItems();
        }
        setUpMap();
        realTimeTracking();
    }

    @EBus
    public void onEventMainThread(UpdateEvent.GroupChanged groupChanged) {
        realTimeTracking();
    }

    @Override
    public void onTrackInfoWindowButton(final String deviceId, String desc) {
        mApplication.setIsFleet(false);
        mApplication.setSelDevice(deviceId);
//        txtDeviceGroup.setText(desc);
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
                getHistory(deviceId, 30*60);
            }
        });

        Button btn60min = (Button) view.findViewById(R.id.btn_historical_60m);
        btn60min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "got clicked on btn60min");
                //--load 30 min historical data
                getHistory(deviceId, 60*60);
            }
        });
        Button btn6h = (Button) view.findViewById(R.id.btn_historical_6h);
        btn6h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "got clicked on btn6h");
                getHistory(deviceId, 6*60*60);
            }
        });
        Button btn12h = (Button) view.findViewById(R.id.btn_historical_12h);
        btn12h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "got clicked on btn6h");
                getHistory(deviceId, 12 * 60 * 60);
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
}

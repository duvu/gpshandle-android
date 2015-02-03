package com.umaps.vtrack.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.umaps.vtrack.R;
import com.umaps.vtrack.cluster.TrackClusterManager;
import com.umaps.vtrack.cluster.TrackInfoWindowAdapter;
import com.umaps.vtrack.cluster.TrackObjectAlgorithm;
import com.umaps.vtrack.cluster.TrackObjectRenderer;
import com.umaps.vtrack.settings.SessionState;
import com.umaps.vtrack.model.MapData;
import com.umaps.vtrack.model.TrackObject;
import com.umaps.vtrack.util.Constants;
import com.umaps.vtrack.util.GpsHandleHTTPAsyncImpl;
import com.umaps.vtrack.util.HTTPDelegateInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class RealTimeFragment extends BaseFragment implements HTTPDelegateInterface {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SupportMapFragment mapFragment;

    private ClusterManager<TrackObject> mClusterManager;
    TimerTask doAsynchronous = null;

    public static RealTimeFragment newInstance(){
        return new RealTimeFragment();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_real_time, container, false);
        setUpMapIfNeeded();
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        startTracking();
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
            setUpMap();
        }
    }

    private void setUpMap() {
        mClusterManager = new ClusterManager<TrackObject>(getActivity().getApplicationContext(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setRenderer(new TrackObjectRenderer(getActivity(), mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterInfoWindowClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterItemClickListener(TrackClusterManager.getInstance());
        mClusterManager.setOnClusterItemInfoWindowClickListener(TrackClusterManager.getInstance());
        mClusterManager.setAlgorithm(new TrackObjectAlgorithm());
    }

//    private GoogleMap reloadMapIfNeeded(){
//        if (mMap==null){
//            setUpMapIfNeeded();
//        }
//        return mMap;
//    }
    private void startTracking(){
        setUpMapIfNeeded();
        TrackInfoWindowAdapter trackInfoWindowAdapter = new TrackInfoWindowAdapter(getActivity().getApplicationContext());
        mMap.setInfoWindowAdapter(trackInfoWindowAdapter);
        getRunInterval(/*httpAsync*/);
    }
    @Override
    public void onPause(){
        super.onPause();
        if(doAsynchronous!=null){
            doAsynchronous.cancel();
        }
    }
    private void getRunInterval(/*final GpsHandleHTTPAsyncImpl http*//*,
                                final String accountID, final String userID,
                                final String password, final String groupID*/)
    {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final GpsHandleHTTPAsyncImpl httpAsync = new GpsHandleHTTPAsyncImpl(RealTimeFragment.this);

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
                        String accountID = SessionState.getAccountID();
                        String userID    = SessionState.getUserID();
                        String password  = SessionState.getPassword();
                        String groupID   = SessionState.getGroupID();
                        try {
                            //showProgressDialog(R.string.updated_str);
                            httpAsync.getMapFleet(getActivity(),
                                    accountID,
                                    userID,
                                    password,
                                    Locale.getDefault().getLanguage(),
                                    groupID, null, false, false, false, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronous, 0, 20000);
    }
    boolean isFirst = true;
    @Override
    public void onFinish(JSONObject response) {
        if(!okResult(response)){
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = response.getJSONObject(Constants.KEY_RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (jsonObject == null){
            return;
        }

        MapData mapData = new MapData(jsonObject);
        if(mapData == null){
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds bounds;
        MapData.Point[] pts = mapData.getPoints();
        LatLng lastLatLng = null;
        mClusterManager.clearItems();
        int i = 0;
        for(i=0; i< pts.length; i++){
            LatLng latLng = new LatLng(pts[i].getLatitude(), pts[i].getLongitude());
            builder.include(latLng);
            TrackObject to = null;
            to = new TrackObject(latLng);
            to.setPointData(pts[i]);        //Point's Data
            mClusterManager.addItem(to);
        }
        float zoom = 0;
        CameraUpdate cu = null;
        if(isFirst){
            if(i>1){
                bounds = builder.build();
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 100 /*padding*/);
                //mMap.moveCamera(cu);
            } else{
                zoom = 14;
                cu = CameraUpdateFactory.newLatLngZoom(lastLatLng, zoom);
            }
            isFirst = false;
        }else {
            zoom = mMap.getCameraPosition().zoom;
            lastLatLng = mMap.getCameraPosition().target;
            cu = CameraUpdateFactory.newLatLngZoom(lastLatLng, zoom);
        }
        mMap.moveCamera(cu);
    }
}

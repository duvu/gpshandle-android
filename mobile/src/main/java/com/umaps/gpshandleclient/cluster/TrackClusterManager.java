package com.umaps.gpshandleclient.cluster;

import android.util.Log;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.umaps.gpshandleclient.model.TrackObject;

/**
 * Created by vu@umaps.vn on 27/01/2015.
 */
public class TrackClusterManager implements
        ClusterManager.OnClusterClickListener<TrackObject>,
        ClusterManager.OnClusterInfoWindowClickListener<TrackObject>,
        ClusterManager.OnClusterItemClickListener<TrackObject>,
        ClusterManager.OnClusterItemInfoWindowClickListener<TrackObject>
{
    private static TrackClusterManager instance = null;
    private static final String TAG = "TrackClusterManager";
    public static TrackClusterManager getInstance(){
        if (instance == null){
            instance = new TrackClusterManager();
        }
        return instance;
    }
    @Override
    public boolean onClusterClick(Cluster<TrackObject> trackCluster) {
        Log.i(TAG, "onClusterClick");
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<TrackObject> trackObjectCluster) {
        Log.i(TAG, "onClusterInfoWindowClick");
        //show list device of this cluster
    }

    @Override
    public boolean onClusterItemClick(TrackObject trackObject) {
        Log.i(TAG, "onClusterItemClick");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(TrackObject trackObject) {
        Log.i(TAG, "onClusterItemInfoWindowClick");
    }
}

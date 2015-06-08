package com.umaps.gpshandleclient.cluster;

import android.util.Log;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.umaps.gpshandleclient.model.TrackItem;

/**
 * Created by vu@umaps.vn on 27/01/2015.
 */
public class TrackClusterManager implements
        ClusterManager.OnClusterClickListener<TrackItem>,
        ClusterManager.OnClusterInfoWindowClickListener<TrackItem>,
        ClusterManager.OnClusterItemClickListener<TrackItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<TrackItem>
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
    public boolean onClusterClick(Cluster<TrackItem> trackCluster) {
        Log.i(TAG, "onClusterClick");
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<TrackItem> trackObjectCluster) {
        Log.i(TAG, "onClusterInfoWindowClick");
        //show list device of this cluster
    }

    @Override
    public boolean onClusterItemClick(TrackItem trackItem) {
        Log.i(TAG, "onClusterItemClick");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(TrackItem trackItem) {
        Log.i(TAG, "onClusterItemInfoWindowClick");
    }
}

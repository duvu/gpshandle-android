package com.umaps.gpshandleclient.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by vu@umaps.vn on 26/01/2015.
 */
public class TrackItem implements ClusterItem{

    private LatLng mPosition;
    private MapPoint data;

    public TrackItem(MapPoint mapPoint){
        this.data = mapPoint;
        this.mPosition = new LatLng(mapPoint.getLatitude(), mapPoint.getLongitude());
    }

    public TrackItem(LatLng mPosition) {
        this.mPosition = mPosition;
    }
    @Override
    public LatLng getPosition() {
        return this.mPosition;
    }

    public MapPoint getPointData() {
        return data;
    }

    public void setPointData(MapPoint pData) {
        this.data = pData;
    }
}

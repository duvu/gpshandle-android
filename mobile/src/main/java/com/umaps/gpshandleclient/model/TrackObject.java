package com.umaps.gpshandleclient.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by vu@umaps.vn on 26/01/2015.
 */
public class TrackObject implements ClusterItem{

    private final LatLng mPosition;
    private MapData.Point pData;

    public TrackObject(LatLng mPosition) {
        this.mPosition = mPosition;
    }
    public TrackObject(double lat, double lng){
        this.mPosition = new LatLng(lat, lng);
    }
    @Override
    public LatLng getPosition() {
        return this.mPosition;
    }

    public MapData.Point getPointData() {
        return pData;
    }

    public void setPointData(MapData.Point pData) {
        this.pData = pData;
    }
}

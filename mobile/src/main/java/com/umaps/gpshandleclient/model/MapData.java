package com.umaps.gpshandleclient.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beou on 29/10/2014.
 */
public class MapData {
    private static final String MAPPING_DATA_SETS       = "DataSets";
    private static final String MAPPING_DATA_POINTS     = "Points";
    private MapPoint[] points;
    public MapPoint[] getPoints() {
        return points;
    }
    public void setPoints(MapPoint[] points) {
        this.points = points;
    }
    public MapData(JSONObject jsonData){
        try {
            List<MapPoint> pts = new ArrayList<>();
            JSONArray dataSets = jsonData.getJSONArray(MAPPING_DATA_SETS);
            for (int j=0; j < dataSets.length(); j++){
                JSONArray _pts = dataSets.getJSONObject(j).getJSONArray(MAPPING_DATA_POINTS);
                for(int i=0; i<_pts.length(); i++){
                    MapPoint pt = new MapPoint(_pts.getString(i));
                    pts.add(pt);
                }
            }
            this.setPoints(pts.toArray(new MapPoint[pts.size()]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

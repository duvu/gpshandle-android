package com.umaps.gpshandleclient.cluster;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.umaps.gpshandleclient.model.MapData;
import com.umaps.gpshandleclient.model.MapPoint;
import com.umaps.gpshandleclient.model.TrackItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 27/01/2015.
 */
public class TrackClusterRenderer extends DefaultClusterRenderer<TrackItem> implements ClusterConstant{
    private Context context;

    public TrackClusterRenderer(Context context, GoogleMap map, ClusterManager<TrackItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }
    @Override
    protected void onBeforeClusterItemRendered(TrackItem trackItem, MarkerOptions options){
        //Draw single icon
        //Set the info-window
        MapPoint mPoint = trackItem.getPointData();
        JSONObject snippet = new JSONObject();
        try {
            snippet.put(IS_CLUSTER, false);
            snippet.put(ITEM_DATA, mPoint.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        options.snippet(snippet.toString());
        options.icon(
            BitmapDescriptorFactory.fromBitmap(
                MarkerTool.getMarker(
                    this.context,
                    mPoint.getIcon(),
                    mPoint.getDesc(),
                    mPoint.getSpeedKPH(),
                    mPoint.getHeading()
                )
            )
        );
        super.onBeforeClusterItemRendered(trackItem, options);
    }
    @Override
    protected void onBeforeClusterRendered(Cluster<TrackItem> cluster, MarkerOptions options) {
        Object[] items = cluster.getItems().toArray();

        JSONObject snippet = new JSONObject();
        JSONObject clusterData = new JSONObject();
        try {
            snippet.put(IS_CLUSTER, true);
            //--build clusterData
            double averageSpeed = 0D;
            int runningCount = 0;
            JSONArray deviceArray = new JSONArray();
            for (Object item : items){
                MapPoint pt = ((TrackItem)item).getPointData();
                deviceArray.put(pt.toString());//[pt1, pt2 ...]
                if (pt.getSpeedKPH() > 0){
                    runningCount++;
                    averageSpeed+=pt.getSpeedKPH();
                }
            }
            double mSpeed = (runningCount!=0)?(averageSpeed/runningCount):0;

            clusterData.put(CLUSTER_SIZE, cluster.getSize());
            clusterData.put(LIVING_COUNT, runningCount);
            clusterData.put(AVERAGE_SPEED, mSpeed);
            clusterData.put(POINTS_LIST, deviceArray);
            snippet.put(ITEM_DATA, clusterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        options.snippet(snippet.toString());
        super.onBeforeClusterRendered(cluster, options);
    }
    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > MIN_ITEMS_RENDER;
    }
}

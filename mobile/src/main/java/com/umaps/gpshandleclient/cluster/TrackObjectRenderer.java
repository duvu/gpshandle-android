package com.umaps.gpshandleclient.cluster;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.umaps.gpshandleclient.model.MapData;
import com.umaps.gpshandleclient.model.TrackObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vu@umaps.vn on 27/01/2015.
 */
public class TrackObjectRenderer extends DefaultClusterRenderer<TrackObject> {
    private static final int MIN_ITEMS_RENDER   = 3;
//    private IconGenerator mIconGenerator;
//    private IconGenerator mClusterIconGenerator;
//    private ImageView mImageView;
//    private ImageView mClusterImageView;
//    private int mDimension;

    public TrackObjectRenderer(Context context, GoogleMap map, ClusterManager<TrackObject> clusterManager) {
        super(context, map, clusterManager);

//        View multiObject = LayoutInflater.from(context).inflate(R.layout.cluster_group_popup, null);
//        mClusterIconGenerator = new IconGenerator(context);
//        mClusterIconGenerator.setContentView(multiObject);
//        mClusterImageView = (ImageView)multiObject.findViewById(R.id.image);

//        mImageView = new
    }
    @Override
    protected void onBeforeClusterItemRendered(TrackObject trackObject, MarkerOptions options){
        //Draw single icon
        //Set the info-window
        JSONObject snippet = new JSONObject();
        try {
            snippet.put(MapData.MD_IS_CLUSTER, false);
            snippet.put(MapData.MD_DAtA, trackObject.getPointData().toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        options.snippet(snippet.toString());
        super.onBeforeClusterItemRendered(trackObject, options);
    }
    @Override
    protected void onBeforeClusterRendered(Cluster<TrackObject> cluster, MarkerOptions options) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).

        //-- build Info for InfoWindow
        //{
        //   isCluster:true,
        //   data:{
        //
        //        }
        // }
        Object[] items = cluster.getItems().toArray();
        JSONObject snippet = new JSONObject();
        JSONObject clusterData = new JSONObject();
        try {
            snippet.put(MapData.MD_IS_CLUSTER, true);
            //--build clusterData
            double averageSpeed = 0D;
            int runningCount = 0;
//            List<Device> deviceList = new ArrayList<>();
            JSONArray deviceArray = new JSONArray();
            for (Object item : items){
                MapData.Point pt = ((TrackObject)item).getPointData();
//                deviceList.add(new Device(pt.getId(), pt.getDeviceDescription(), pt.getEpoch(), pt.getAddress()));
                JSONObject ptObject = new JSONObject();
                ptObject.put(MapData.Point.MD_ID, pt.getId());
                ptObject.put(MapData.Point.MD_DESC, pt.getDeviceDescription());
                ptObject.put(MapData.Point.MD_EPOCH, pt.getEpoch());
                ptObject.put(MapData.Point.MD_ADDRESS, pt.getAddress());
                deviceArray.put(ptObject);
                if (pt.getSpeedKPH() > 0){
                    runningCount++;
                    averageSpeed+=pt.getSpeedKPH();
                }
            }
            double mSpeed = (runningCount!=0)?(averageSpeed/runningCount):0;
            clusterData.put(MapData.MD_COUNT, cluster.getSize());
            clusterData.put(MapData.MD_RUNNING_COUNT, runningCount);
            clusterData.put(MapData.MD_AVERAGE_SPEED, mSpeed);
            clusterData.put(MapData.MD_DEVICE_LIST, deviceArray);
            snippet.put(MapData.MD_DAtA, clusterData);
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

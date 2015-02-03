package com.umaps.vtrack.cluster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import com.umaps.vtrack.R;
import com.umaps.vtrack.model.MapData;
import com.umaps.vtrack.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 29/10/2014.
 */
public class TrackInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater = null;
    IconGenerator iconFactory = null;

    public TrackInfoWindowAdapter(Context _context){
        context = _context;
        inflater = (LayoutInflater.from(_context));
        iconFactory = new IconGenerator(_context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        //--check if cluster or item
        String info = marker.getSnippet();
        if (StringTools.isBlank(info)){
            return null;
        }
        //-- info should be not null now
        Log.i("INFO-WINDOW", info);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject==null){
            return null;
        }
        //-- jsonObject should not null now
        try {
            if(jsonObject.getBoolean(MapData.MD_IS_CLUSTER)){
                Log.i("INFO-WINDOW", "You've clicked on a cluster");
                return getInfoWindowForCluster(jsonObject.getJSONObject(MapData.MD_DAtA));
            }
            else {
                return getInfoWindowForItem(jsonObject.getJSONObject(MapData.MD_DAtA));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //-- getInfoWindowForCluster
    public View getInfoWindowForCluster(JSONObject jsonObject) throws JSONException {
        View view = inflater.inflate(R.layout.cluster_group_popup, null);
        int devCount = jsonObject.getInt(MapData.MD_COUNT);
        int runningCount = jsonObject.getInt(MapData.MD_RUNNING_COUNT);
        double averageSpeed = jsonObject.getDouble(MapData.MD_AVERAGE_SPEED);

        TextView devCountText = (TextView) view.findViewById(R.id.deviceCountText);
        devCountText.setText(R.string.deviceCountText);
        TextView devCountTView = (TextView) view.findViewById(R.id.deviceCountValue);
        devCountTView.setText(""+devCount);

        TextView devRunningText = (TextView) view.findViewById(R.id.deviceRunningText);
        devRunningText.setText(R.string.deviceRunningCountText);
        TextView devRunningValue = (TextView) view.findViewById(R.id.deviceRunningValue);
        devRunningValue.setText(""+runningCount);

        TextView devAverageSpeedText = (TextView) view.findViewById(R.id.speedAverageText);
        devAverageSpeedText.setText(R.string.averageSpeedText);
        TextView devAverageSpeedValue = (TextView) view.findViewById(R.id.speedAverageValue);
        devAverageSpeedValue.setText(""+averageSpeed);
        //TODO: do something here for showing window-popup of a cluster
        return view;
    }
    //-- getInfoWindowForItem
    public View getInfoWindowForItem(JSONObject jsonObject) throws JSONException {
        View view = inflater.inflate(R.layout.cluster_item_popup, null);
        double latitude     = jsonObject.getDouble(MapData.Point.MD_LATITUDE);
        double longitude    = jsonObject.getDouble(MapData.Point.MD_LONGITUDE);

        String id           = jsonObject.getString(MapData.Point.MD_ID);
        String vin          = jsonObject.getString(MapData.Point.MD_VIN);
        String desc         = jsonObject.getString(MapData.Point.MD_DESC);
        long epoch          = jsonObject.getLong(MapData.Point.MD_EPOCH);
        String status       = jsonObject.getString(MapData.Point.MD_STATUS);
        String latLon       = latitude+"/"+longitude;
        double gpsAge       = jsonObject.getDouble(MapData.Point.MD_GPS_AGE);
        double creationAge  = jsonObject.getDouble(MapData.Point.MD_CREATION_AGE);
        double accuracy     = jsonObject.getDouble(MapData.Point.MD_ACCURACY);
        int satCount        = jsonObject.getInt(MapData.Point.MD_SAT_COUNT);
        double speed        = jsonObject.getDouble(MapData.Point.MD_SPEED);
        double heading      = jsonObject.getDouble(MapData.Point.MD_HEADING);
        double altitude     = jsonObject.getDouble(MapData.Point.MD_ALTITUDE);
        double odom         = jsonObject.getDouble(MapData.Point.MD_ODOM);
        boolean isStopped   = jsonObject.getBoolean(MapData.Point.MD_IS_STOPPED);
        String gpIo         = jsonObject.getString(MapData.Point.MD_GP_IO);
        String address      = jsonObject.getString(MapData.Point.MD_ADDRESS);

        TextView tID = (TextView) view.findViewById(R.id.tID);
        tID.setText(R.string.tID);
        TextView cID = (TextView) view.findViewById(R.id.cID);
        cID.setText(id);

        TextView tDesc = (TextView) view.findViewById(R.id.tDesc);
        tDesc.setText(R.string.tDesc);
        TextView cDesc = (TextView) view.findViewById(R.id.cDesc);
        cDesc.setText(desc);

        TextView titlePosition = (TextView) view.findViewById(R.id.titlePosition);
        titlePosition.setText(R.string.title_position);
        TextView contentPosition = (TextView) view.findViewById(R.id.contentPosition);
        contentPosition.setText(latLon);

        TextView tSpeed = (TextView) view.findViewById(R.id.tSpeed);
        tSpeed.setText(R.string.tSpeed);
        TextView cSpeed = (TextView) view.findViewById(R.id.cSpeed);
        cSpeed.setText(speed+"km/h");

        TextView tOdom = (TextView) view.findViewById(R.id.tOdom);
        tOdom.setText(R.string.tOdom);
        TextView cOdom = (TextView) view.findViewById(R.id.cOdom);
        cOdom.setText(odom+"km");

        TextView titleAddr = (TextView) view.findViewById(R.id.titleAddr);
        titleAddr.setText(R.string.title_addr);
        TextView contentAddr = (TextView) view.findViewById(R.id.contentAddr);
        contentAddr.setText(address);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

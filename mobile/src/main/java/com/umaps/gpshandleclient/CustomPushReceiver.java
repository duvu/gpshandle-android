package com.umaps.gpshandleclient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beou on 15/08/2015.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = "CustomPushReceiver";
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        if (intent == null) return;

        try {
            JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.i(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

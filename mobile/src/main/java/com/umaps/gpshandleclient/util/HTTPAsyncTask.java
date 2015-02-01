package com.umaps.gpshandleclient.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by beou on 29/10/2014.
 */
public class HTTPAsyncTask {
    private String TAG = "HttpAsyncTask";

    public static final String APPLICATION_JSON     = "application/json";

    private static HTTPAsyncTask instance = null;
    public static HTTPAsyncTask getInstance(){
        if (instance==null){
            instance = new HTTPAsyncTask();
        }
        return instance;
    }
    //-- initial async client
    private static AsyncHttpClient client = new AsyncHttpClient();

    public void doPost(final Context context, String url, JSONObject jsonPostObject, AsyncHttpResponseHandler responseHandler) {
        Log.i(TAG, jsonPostObject.toString());
        try {
            StringEntity entity = new StringEntity(jsonPostObject.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            client.post(context, url, entity, APPLICATION_JSON, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
    }

    public void doGet(final Context context, String url, AsyncHttpResponseHandler responseHandler){
        Log.i(TAG, url);
    }
}

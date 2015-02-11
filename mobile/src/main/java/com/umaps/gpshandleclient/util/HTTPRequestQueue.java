package com.umaps.gpshandleclient.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by vu@umaps.vn on 05/02/2015.
 */
public class HTTPRequestQueue {
    private static HTTPRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private HTTPRequestQueue(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }
    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public static synchronized HTTPRequestQueue getInstance(Context context){
        if (mInstance == null){
            mInstance = new HTTPRequestQueue(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}

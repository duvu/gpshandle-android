package com.umaps.gpssdk;

/**
 * GPSHandleClient
 * com.umaps.gpssdk
 * Created by beou on 11/11/2015.
 */
public interface GCallback1<T extends Throwable> {
    public void done(T t);
}
package com.umaps.gpssdk;

/**
 * GPSHandleClient
 * com.umaps.gpssdk
 * Created by beou on 11/11/2015.
 */
public interface DeleteCallback extends GCallback1<GException> {
    @Override
    public void done(GException e);
}

package com.umaps.gpssdk;

import com.umaps.gpssdk.model.GObject;

import java.util.List;

/**
 * GPSHandleClient
 * com.umaps.gpssdk
 * Created by beou on 11/11/2015.
 */
public interface FindCallback <T extends GObject> extends GCallback2<List<T>, GException> {
    @Override
    public void done(List<T> list, GException e);
}
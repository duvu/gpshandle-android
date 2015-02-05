package com.umaps.gpshandleclient.views;

import android.view.View;
import android.widget.ExpandableListView;

/**
 * Created by vu@umaps.vn on 01/11/2014.
 */
public class GPSHandleOnGroupClickListener implements ExpandableListView.OnGroupClickListener {

    public GPSHandleOnGroupClickListener(){

    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }
}

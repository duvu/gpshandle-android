package com.umaps.gpshandleclient.ui;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.umaps.gpshandleclient.R;

/**
 * Created by beou on 05/05/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.frag_settings);
//        findPreference(key_interval);
    }
}

package com.umaps.gpshandleclient.fragment;

import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.umaps.gpshandleclient.R;

import java.util.List;

public class SettingsFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

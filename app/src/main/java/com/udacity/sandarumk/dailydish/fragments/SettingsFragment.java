package com.udacity.sandarumk.dailydish.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.udacity.sandarumk.dailydish.R;


public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences,rootKey);
    }
}

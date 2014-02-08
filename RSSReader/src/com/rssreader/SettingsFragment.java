package com.rssreader;

import com.rssreader.core.FeedsAutoRefresher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {

    private OnSharedPreferenceChangeListener listener;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				String refreshRatePrefKey = getString(R.string.pref_refresh_rate_key);
				if (key.equals(refreshRatePrefKey)) {
					Context appContext = getActivity().getApplicationContext();
					FeedsAutoRefresher.SetAlarm(appContext);
				}
			}
		};
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		prefs.unregisterOnSharedPreferenceChangeListener(listener);
	}
}

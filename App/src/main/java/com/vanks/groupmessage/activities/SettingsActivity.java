package com.vanks.groupmessage.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.vanks.groupmessage.R;

/**
 * Created by vaneyck on 11/30/15.
 */
public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}

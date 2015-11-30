package com.vanks.groupmessage.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.utils.PreferenceUtil;
import com.vanks.groupmessage.utils.ScheduleUtil;

/**
 * Created by vaneyck on 11/30/15.
 */
public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
	}

	SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Context context = getApplicationContext();
			if (key.equals(PreferenceUtil.APP_ON) && PreferenceUtil.isAppOn(context)) {
				ScheduleUtil.scheduleMessageSendService(context);
			} else {
				ScheduleUtil.cancelMessageSendServiceAlarm(context);
			}
		}
	};
}

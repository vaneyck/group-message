package com.vanks.groupmessage.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by vaneyck on 11/29/15.
 */
public class PreferenceUtil {
	public static final String DISPATCH_DELAY = "dispatch.delay";

	/**
	 * returns the duration to wait (in seconds) before sending next dispatch
	 * defaults to 7 seconds
	 * @param context
	 * @return
	 */
	public static int getDispatchDelay (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(DISPATCH_DELAY, 7);
	}
}

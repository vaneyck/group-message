package com.vanks.groupmessage.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by vaneyck on 11/29/15.
 */
public class PreferenceUtil {
	public static final String DISPATCH_DELAY = "dispatch.delay";
	public static final String BATCH_DISPATCH_DELAY = "batch.dispatch.delay";
	public static final String DISPATCH_PICKUP_SIZE = "dispatch.pickup.size";
	public static final String APP_ON = "app.on";

	/**
	 * returns the duration to wait (in seconds) before sending next dispatch
	 * defaults to 7 seconds
	 * @param context
	 * @return
	 */
	public static int getDispatchDelay (Context context) {
		String delay = PreferenceManager.getDefaultSharedPreferences(context).getString(DISPATCH_DELAY, "7");
		return Integer.parseInt(delay);
	}

	public static int getBatchDispatchDelay (Context context) {
		String delay = PreferenceManager.getDefaultSharedPreferences(context).getString(BATCH_DISPATCH_DELAY, "60");
		return Integer.parseInt(delay);
	}

	public static int getDispatchPickupSize(Context context) {
		String pickupSize = PreferenceManager.getDefaultSharedPreferences(context).getString(DISPATCH_PICKUP_SIZE, "10");
		return Integer.parseInt(pickupSize);
	}

	public static boolean isAppOn (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(APP_ON, true);
	}
}

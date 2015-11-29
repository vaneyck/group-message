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

	/**
	 * returns the duration to wait (in seconds) before sending next dispatch
	 * defaults to 7 seconds
	 * @param context
	 * @return
	 */
	public static int getDispatchDelay (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(DISPATCH_DELAY, 7);
	}

	public static int getBatchDispatchDelay (Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(BATCH_DISPATCH_DELAY, 60);
	}

	public static int getDispatchPickupSize(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(DISPATCH_PICKUP_SIZE, 10);
	}
}

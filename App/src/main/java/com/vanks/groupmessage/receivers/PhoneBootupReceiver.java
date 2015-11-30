package com.vanks.groupmessage.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vanks.groupmessage.utils.PreferenceUtil;
import com.vanks.groupmessage.utils.ScheduleUtil;

/**
 * Created by vaneyck on 11/30/15.
 */
public class PhoneBootupReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ScheduleUtil.scheduleMessageSendService(context);
	}
}

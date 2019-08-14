package com.vanks.groupmessage.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vanks.groupmessage.enums.DispatchStatus;
import com.vanks.groupmessage.utils.DispatchUtil;

/**
 * Created by vaneyck on 12/3/15.
 */
public class SmsDeliveredBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent intent) {
		long dispatchId = intent.getExtras().getLong("dispatchId");
		Log.i("SmsDelivered", "dispatch sent : " + dispatchId);
		switch (getResultCode()) {
			case Activity.RESULT_OK:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.DELIVERED);
				break;
			case Activity.RESULT_CANCELED:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.NOT_DELIVERED);
				break;
		}
	}
}

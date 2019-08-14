package com.vanks.groupmessage.receivers;

/**
 * Created by vaneyck on 11/21/15.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.vanks.groupmessage.enums.DispatchStatus;
import com.vanks.groupmessage.utils.DispatchUtil;

/**
 * Created by vaneyck on 3/4/14.
 */
public class SmsSentBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		long dispatchId = intent.getExtras().getLong("dispatchId");
		Log.i("SmsSent", "dispatch sent : " + dispatchId);
		switch (getResultCode()) {
			case Activity.RESULT_OK:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.SENT);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.FAILED);
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.FAILED);
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.FAILED);
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.FAILED);
				break;
			default:
				DispatchUtil.updateDispatch(dispatchId, DispatchStatus.FAILED);
				break;
		}
	}
}

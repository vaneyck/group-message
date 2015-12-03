package com.vanks.groupmessage.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.vanks.groupmessage.enums.DispatchStatus;
import com.vanks.groupmessage.models.persisted.Dispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class DispatchUtil {
	public static final String SENT_INTENT = "com.vanks.dispatch.SMS_SENT";
	private static final String DELIVERED_INTENT = "com.vanks.dispatch.SMS_DELIVERED";

	public static Integer statusCount(List<Dispatch> dispatchList, DispatchStatus status) {
		int count = 0;
		for (Dispatch dispatch : dispatchList) {
			if(dispatch.getStatus() == status) {
				count++;
			}
		}
		return count;
	}

	public static void sendDispatch (Context context, Dispatch dispatch) {
		if(!PreferenceUtil.isAppOn(context)) {
			Log.i("DispatchUtil", "Marking dispatch as FAILED since app has been turned off");
			dispatch.setStatus(DispatchStatus.FAILED);
			dispatch.save();
			return;
		}
		String text = dispatch.getMessage().getText();
		String destinationNumber = dispatch.getPhoneNumber();
		long dispatchId = dispatch.getId();

		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> multipartMessageList = smsManager.divideMessage(text);
		ArrayList<PendingIntent> sentPendingIntentsList = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveredPendingIntentsList = new ArrayList<PendingIntent>();

		Intent sentIntent = new Intent(SENT_INTENT);
		Intent deliveredIntent = new Intent(DELIVERED_INTENT);
		sentIntent.putExtra("dispatchId", dispatchId);

		for (int x = 0; x <= multipartMessageList.size(); x++) {
			int uniqueBroadcastId = (int) (dispatchId * new Date().getTime());
			sentPendingIntentsList.add(PendingIntent.getBroadcast(context, uniqueBroadcastId, sentIntent, 0));
			deliveredPendingIntentsList.add(PendingIntent.getBroadcast(context, uniqueBroadcastId, deliveredIntent, 0));
		}
		smsManager.sendMultipartTextMessage(destinationNumber, null, multipartMessageList, sentPendingIntentsList, deliveredPendingIntentsList);
	}

	public static List<Dispatch> getDispatchesToSend(Context context) {
		Integer dispatchPayloadSize = PreferenceUtil.getDispatchPickupSize(context);
		return Select.from(Dispatch.class)
				.where(Condition.prop("status").eq(DispatchStatus.QUEUED.toString()))
				.limit(dispatchPayloadSize.toString())
				.list();
	}

	public static void updateDispatch (Long dispatchId, DispatchStatus status) {
		Dispatch dispatch = Dispatch.findById(Dispatch.class, dispatchId);
		updateDispatch(dispatch, status);
	}

	public static void updateDispatch (Dispatch dispatch, DispatchStatus status) {
		dispatch.setStatus(status);
		dispatch.setAttempted(true);
		dispatch.setDateAttempted(new Date().getTime());
		dispatch.save();
	}
}

package com.vanks.groupmessage.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

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

	public static int sentCount(List<Dispatch> dispatchList) {
		int count = 0;
		for (Dispatch dispatch : dispatchList) {
			if(dispatch.getStatus() == DispatchStatus.SENT) {
				count++;
			}
		}
		return count;
	}

	public static void sendDispatch (Context context, Dispatch dispatch) {
		String text = dispatch.getMessage().getText();
		String destinationNumber = dispatch.getPhoneNumber();
		long dispatchId = dispatch.getId();

		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> multipartMessageList = smsManager.divideMessage(text);
		ArrayList<PendingIntent> sentPendingIntentsList = new ArrayList<PendingIntent>();

		Intent sentIntent = new Intent(SENT_INTENT);
		sentIntent.putExtra("dispatchId", dispatchId);

		for (int x = 0; x <= multipartMessageList.size(); x++) {
			int uniqueBroadcastId = (int) (dispatchId * new Date().getTime());
			sentPendingIntentsList.add(PendingIntent.getBroadcast(context, uniqueBroadcastId, sentIntent, 0));
		}
		smsManager.sendMultipartTextMessage(destinationNumber, null, multipartMessageList, sentPendingIntentsList, null);
	}

	public static void updateDispatch (Long dispatchId, DispatchStatus status) {
		Dispatch dispatch = Dispatch.findById(Dispatch.class, dispatchId);
		dispatch.setStatus(status);
		dispatch.save();
	}
}

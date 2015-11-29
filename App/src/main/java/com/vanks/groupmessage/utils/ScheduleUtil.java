package com.vanks.groupmessage.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vanks.groupmessage.services.MessageSendService;

import java.util.Date;

/**
 * Created by vaneyck on 11/29/15.
 */
public class ScheduleUtil {

	private static int MESSAGE_SEND_SERVICE_ALARM_ID = 4;
	/**
	 * Schedule the MessageSendService to run after the given duration
	 * @param context
	 * @param duration
	 */
	public static void scheduleMessageSendService(Context context, int duration) {
		cancelMessageSendServiceAlarm(context);
		scheduleMessageSendServiceAlarm(context, duration);
	}

	private static void cancelMessageSendServiceAlarm(Context context) {
		Log.i("MessageSendService", "Canceling alarm at " + new Date());
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getMessageSendServiceIntent(context));
	}


	private static void scheduleMessageSendServiceAlarm(Context context, long interval) {
		if(messageSendServiceAlarmRunning(context)) {
			Log.i("MessageSendService", "MessageSendServiceAlarm running. Will not setup a new one");
			return;
		}

		long timeToRun = System.currentTimeMillis() + interval;

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeToRun, getMessageSendServiceIntent(context));
		Log.i("MessageSendService", "UploadService Alarm scheduled for : " + new Date(timeToRun));
	}

	private static boolean messageSendServiceAlarmRunning(Context context) {
		return (PendingIntent.getService(context, 0, new Intent(context, MessageSendService.class), PendingIntent.FLAG_NO_CREATE) != null);
	}

	private static PendingIntent getMessageSendServiceIntent (Context context) {
		Intent intent = new Intent(context, MessageSendService.class);
		return PendingIntent.getService(context, MESSAGE_SEND_SERVICE_ALARM_ID, intent, 0);
	}
}

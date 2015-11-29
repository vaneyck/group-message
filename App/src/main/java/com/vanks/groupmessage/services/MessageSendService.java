package com.vanks.groupmessage.services;

import android.app.IntentService;
import android.content.Intent;

import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.utils.DispatchUtil;
import com.vanks.groupmessage.utils.PreferenceUtil;
import com.vanks.groupmessage.utils.ScheduleUtil;

import java.util.List;

public class MessageSendService extends IntentService {

	static boolean isRunning = false;

	public MessageSendService() {
		super("MessageSendService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			retrieveAndSendDispatches();
			queueMessageSendService();
		}
	}

	private void retrieveAndSendDispatches () {
		List<Dispatch> dispatchListToSend = DispatchUtil.getDispatchesToSend();
		for (Dispatch dispatch : dispatchListToSend) {
			sendDispatch(dispatch);
		}
	}

	private void sendDispatch (Dispatch dispatch) {
		DispatchUtil.sendDispatch(getApplicationContext(), dispatch);

		int delay = PreferenceUtil.getDispatchDelay(getApplicationContext()) * 1000;
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void queueMessageSendService () {
		//TODO default to 60 seconds
		ScheduleUtil.scheduleMessageSendService(getApplicationContext(), 60);
	}
}

package com.vanks.groupmessage.services;

import android.app.IntentService;
import android.content.Intent;

public class MessageSendService extends IntentService {

	static boolean isRunning = false;

	public MessageSendService() {
		super("MessageSendService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			//TODO
		}
	}
}

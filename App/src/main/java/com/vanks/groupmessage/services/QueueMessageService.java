package com.vanks.groupmessage.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.vanks.groupmessage.activities.CreateMessageActivity;
import com.vanks.groupmessage.activities.MainActivity;
import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.models.persisted.Message;
import com.vanks.groupmessage.models.unsaved.Contact;
import com.vanks.groupmessage.utils.GroupUtil;
import com.vanks.groupmessage.utils.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class QueueMessageService extends IntentService {

	public QueueMessageService() {
		super("QueueMessageService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			Long groupId = intent.getLongExtra("groupId", 0);
			String groupName = intent.getStringExtra("groupName");
			String messageToSend = intent.getStringExtra("messageToSend");
			ArrayList<Contact> contactArrayList = GroupUtil.getContactsInGroup(this, groupId);
			queueGroupMessageForSending(messageToSend, groupId, groupName, contactArrayList);
			sendBroadcast(new Intent(CreateMessageActivity.MESSAGE_SAVED_INTENT));
		}
	}

	/**
	 * Store the message into database for sending later on
	 * @param messageToSend
	 * @param groupId
	 * @param groupName
	 * @param contactList
	 */
	private void queueGroupMessageForSending (String messageToSend, Long groupId, String groupName, List<Contact> contactList) {
		Message message = new Message(messageToSend, groupId, groupName);
		message.save();
		for (Contact contact : contactList) {
			Dispatch dispatch = new Dispatch(contact.getPhoneNumber(), contact.getName(), message);
			dispatch.save();
		}
		sendBroadcast(new Intent(MainActivity.REFRESH_UI_INTENT_FILTER));
	}
}

package com.vanks.groupmessage.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.vanks.groupmessage.activities.MainActivity;
import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.models.persisted.Message;
import com.vanks.groupmessage.models.unsaved.Contact;
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
			ArrayList<Contact> contactArrayList = getContactsInGroup(groupId);
			queueGroupMessageForSending(messageToSend, groupId, groupName, contactArrayList);
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

	/**
	 * Retrieve contacts given the group id
	 * @param groupId
	 * @return
	 */
	private ArrayList<Contact> getContactsInGroup (Long groupId) {
		ArrayList<Contact> contactArrayList = new ArrayList<>();
		ArrayList<String> phoneNumberList = new ArrayList<>();
		String[] cProjection = { ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

		Cursor groupCursor = getContentResolver().query(
				ContactsContract.Data.CONTENT_URI,
				cProjection,
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "= ?" + " AND "
						+ ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
						+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'",
				new String[] { String.valueOf(groupId) }, null);
		if (groupCursor != null && groupCursor.moveToFirst()) {
			do {
				int nameCoumnIndex = groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				String name = groupCursor.getString(nameCoumnIndex);
				long contactId = groupCursor.getLong(groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
				Cursor numberCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
				if (numberCursor.moveToFirst()) {
					int numberColumnIndex = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					do
					{
						String phoneNumber = numberCursor.getString(numberColumnIndex);
						phoneNumber = phoneNumber.replace(" ", "").trim();
						phoneNumber = PhoneNumberUtils.getInternationalPhoneNumber(getApplicationContext(), phoneNumber, false);
						if(!phoneNumberList.contains(phoneNumber)) {
							Log.d("CreateMessageActivity", "contact " + name + ":" + phoneNumber);
							contactArrayList.add(new Contact(name, phoneNumber));
							phoneNumberList.add(phoneNumber);
						}
					} while (numberCursor.moveToNext());
					numberCursor.close();
				}
			} while (groupCursor.moveToNext());
			groupCursor.close();
		}
		return contactArrayList;
	}
}

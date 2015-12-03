package com.vanks.groupmessage.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.arrayadapters.create.GroupArrayAdapter;
import com.vanks.groupmessage.models.unsaved.Contact;
import com.vanks.groupmessage.models.unsaved.Group;
import com.vanks.groupmessage.models.persisted.Message;
import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.services.MessageSendService;

import java.util.ArrayList;
import java.util.List;

/**
* Created by vaneyck on 11/21/15.
*/
public class CreateMessageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	Spinner groupListSpinner;
	EditText messageToSendEditText;
	Button queueMessageForSendingButton;
	ArrayList<Group> groupArrayList;
	GroupArrayAdapter groupArrayAdapter;

	private static final int URL_LOADER = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);
		getSupportLoaderManager().initLoader(URL_LOADER, null, this);
	}

	@Override
	public void onResume () {
		super.onResume();
		groupArrayList = new ArrayList<>();
		groupListSpinner = (Spinner) findViewById(R.id.groupListSpinner);
		messageToSendEditText = (EditText) findViewById(R.id.messageToSendTextView);
		queueMessageForSendingButton = (Button) findViewById(R.id.submitMessageForSendingButton);
		queueMessageForSendingButton.setOnClickListener(showConfirmSendDialog);
	}

	private void initialiseUi () {
		groupArrayAdapter = new GroupArrayAdapter(this, R.layout.activity_group_list_item, groupArrayList);
		groupListSpinner.setAdapter(groupArrayAdapter);
		groupArrayAdapter.notifyDataSetChanged();
	}

	View.OnClickListener showConfirmSendDialog = new View.OnClickListener() {
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(CreateMessageActivity.this);
			builder.setMessage(R.string.confirm_send_message)
					.setTitle(R.string.confirm_send_message_title);
			builder.setPositiveButton(R.string.send_label, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					queueMessage();
				}
			});
			builder.setNegativeButton(R.string.dont_send, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	};

	public void queueMessage () {
		int index = groupListSpinner.getSelectedItemPosition();
		Group selectedGroup = groupArrayList.get(index);
		String messageToSend = messageToSendEditText.getText().toString();
		ArrayList<Contact> contactArrayList = getContactsInGroup(selectedGroup);
		queueGroupMessageForSending(messageToSend, selectedGroup, contactArrayList);
	}

	//>LoaderManager.LoaderCallbacks<Cursor> interface methods
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Uri uri = ContactsContract.Groups.CONTENT_SUMMARY_URI;
		String[] projection = null;
		String selection = ContactsContract.Groups.ACCOUNT_TYPE + " NOT NULL AND " +
				ContactsContract.Groups.ACCOUNT_NAME + " NOT NULL AND " + ContactsContract.Groups.DELETED + "=0";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			selection += " AND " + ContactsContract.Groups.AUTO_ADD + "=0 AND " + ContactsContract.Groups.FAVORITES + "=0";
		}

		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Groups.TITLE + " ASC";
		Loader<Cursor> loader = new CursorLoader(getApplicationContext(), uri, projection, selection, selectionArgs, sortOrder);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		if(cursor == null || cursor.getCount() == 0) { return; }
		cursor.moveToFirst();
		do {
			String groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
			Long groupId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
			groupArrayList.add(new Group(groupName, groupId));
			initialiseUi();
		} while(cursor.moveToNext());
		cursor.close();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {}

	/**
	 * Retrieve contacts given the group id
	 * @param group
	 * @return
	 */
	private ArrayList<Contact> getContactsInGroup (Group group) {
		ArrayList<Contact> contactArrayList = new ArrayList<>();
		ArrayList<String> phoneNumberList = new ArrayList<>();
		Long groupId = group.getId();
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

	/**
	 * Store the message into database for sending later on
	 * @param messageToSend
	 * @param group
	 * @param contactList
	 */
	private void queueGroupMessageForSending (String messageToSend,Group group, List<Contact> contactList) {
		Message message = new Message(messageToSend, group.getId(), group.getName());
		message.save();
		for (Contact contact : contactList) {
			Dispatch dispatch = new Dispatch(contact.getPhoneNumber(), contact.getName(), message);
			dispatch.save();
		}
		Toast.makeText(getApplicationContext(), "Message queued for sending", Toast.LENGTH_LONG).show();
		startService(new Intent(getApplicationContext(), MessageSendService.class));
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
}

package com.vanks.groupmessage.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vanks.groupmessage.R;
import com.vanks.groupmessage.arrayadapters.create.GroupArrayAdapter;
import com.vanks.groupmessage.models.unsaved.Group;
import com.vanks.groupmessage.services.MessageSendService;
import com.vanks.groupmessage.services.QueueMessageService;
import com.vanks.groupmessage.utils.GroupUtil;

import java.util.ArrayList;

/**
* Created by vaneyck on 11/21/15.
*/
public class CreateMessageActivity extends AppCompatActivity {

	Spinner groupListSpinner;
	EditText messageToSendEditText;
	Button queueMessageForSendingButton;
	ArrayList<Group> groupArrayList;
	GroupArrayAdapter groupArrayAdapter;
	Dialog showSavingProgressDialog;

	public static final String MESSAGE_SAVED_INTENT = "message.saved.intent";
	private final String CURRENT_SPINNER_INDEX = "spinner.index";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);
	}

	@Override
	public void onResume () {
		super.onResume();
		groupArrayList = new ArrayList<>();
		groupListSpinner = (Spinner) findViewById(R.id.groupListSpinner);;
		messageToSendEditText = (EditText) findViewById(R.id.messageToSendTextView);
		queueMessageForSendingButton = (Button) findViewById(R.id.submitMessageForSendingButton);
		queueMessageForSendingButton.setOnClickListener(showConfirmSendDialog);
		retrieveGroups();
		initialiseUi();
	}

	@Override
	public void onPause() {
		super.onPause();
		int index = groupListSpinner.getSelectedItemPosition();
		storeCurrentSelectedGroupIndex(index);
	}

	private void initialiseUi () {
		groupArrayAdapter = new GroupArrayAdapter(this, R.layout.activity_group_list_item, groupArrayList);
		groupListSpinner.setAdapter(groupArrayAdapter);
		groupArrayAdapter.notifyDataSetChanged();
		groupListSpinner.setSelection(retrieveCurrentSelectedGroupIndex());
	}

	View.OnClickListener showConfirmSendDialog = new View.OnClickListener() {
		public void onClick(View v) {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CreateMessageActivity.this);
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

	private Dialog showSendingProgressDialog () {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CreateMessageActivity.this);
		LayoutInflater inflater = getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_save_message_progress, null));
		builder.setTitle(R.string.saving_message_label);
		androidx.appcompat.app.AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	public void queueMessage () {
		int index = groupListSpinner.getSelectedItemPosition();
		Group selectedGroup = groupArrayList.get(index);
		String messageToSend = messageToSendEditText.getText().toString();

		showSavingProgressDialog = showSendingProgressDialog();
		registerReceiver(dismissProgressDialogReceiver, dismissProgressDialogIntentFilter);

		//queue messages for sending
		Intent intent = new Intent(getApplicationContext(), QueueMessageService.class);
		intent.putExtra("messageToSend", messageToSend);
		intent.putExtra("groupId", selectedGroup.getId());
		intent.putExtra("groupName", selectedGroup.getName());
		startService(intent);
	}

	private BroadcastReceiver dismissProgressDialogReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			showSavingProgressDialog.dismiss();
			unregisterReceiver(dismissProgressDialogReceiver);
			// schedule message sending
			startService(new Intent(getApplicationContext(), MessageSendService.class));
			// navigate to the main screen
			Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
			mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(mainActivityIntent);
		}
	};

	private IntentFilter dismissProgressDialogIntentFilter = new IntentFilter(MESSAGE_SAVED_INTENT);

	private void storeCurrentSelectedGroupIndex (int index) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(CURRENT_SPINNER_INDEX, index);
		editor.commit();
	}

	private int retrieveCurrentSelectedGroupIndex () {
		return PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt(CURRENT_SPINNER_INDEX, 0);
	}

	/**
	 * retrieve the groups and store them in $groupArrayList
	 */
	private void retrieveGroups () {
		String selection = ContactsContract.Groups.ACCOUNT_TYPE + " NOT NULL AND " +
				ContactsContract.Groups.ACCOUNT_NAME + " NOT NULL AND " + ContactsContract.Groups.DELETED + "=0";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			selection += " AND " + ContactsContract.Groups.AUTO_ADD + "=0 AND " + ContactsContract.Groups.FAVORITES + "=0";
		}
		Cursor cursor = getBaseContext().getContentResolver().query(
				ContactsContract.Groups.CONTENT_SUMMARY_URI, // data Uri
				null,
				selection, // desired fields
				null, // query parameters
				ContactsContract.Groups.TITLE + " ASC" // sorting
		);

		// retrieve the groups
		if(cursor == null || cursor.getCount() == 0) { return; }
		cursor.moveToFirst();
		do {
			String groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
			Long groupId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
			Long groupCount = GroupUtil.getGroupCount(this, groupId);
			if (groupCount > 0) {
				groupArrayList.add(new Group(groupName, groupId, groupCount));
			}
		} while(cursor.moveToNext());
		cursor.close();
	}
}

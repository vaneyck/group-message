package com.vanks.groupmessage.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
public class CreateMessageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	Spinner groupListSpinner;
	EditText messageToSendEditText;
	Button queueMessageForSendingButton;
	ArrayList<Group> groupArrayList;
	GroupArrayAdapter groupArrayAdapter;
	Dialog showSavingProgressDialog;

	private static final int URL_LOADER = 0;
	public static final String MESSAGE_SAVED_INTENT = "message.saved.intent";

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

	private Dialog showSendingProgressDialog () {
		AlertDialog.Builder builder = new AlertDialog.Builder(CreateMessageActivity.this);
		LayoutInflater inflater = getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_save_message_progress, null));
		builder.setTitle(R.string.saving_message_label);
		AlertDialog dialog = builder.create();
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
			groupArrayList.add(new Group(groupName, groupId, GroupUtil.getGroupCount(this, groupId)));
			initialiseUi();
		} while(cursor.moveToNext());
		cursor.close();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {}
}

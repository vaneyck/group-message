package com.vanks.groupmessage.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.arrayadapters.view.DispatchArrayAdapter;
import com.vanks.groupmessage.enums.DispatchStatus;
import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.models.persisted.Message;
import com.vanks.groupmessage.utils.DispatchUtil;

import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class ViewMessageActivity extends AppCompatActivity {

	Message message;
	TextView groupNameTextView, messageTextView, sentCountTextView, failedCountTextView, pendingCountTextView;
	ListView dispatchListView;
	DispatchArrayAdapter dispatchArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_message);
		Long messageId = getIntent().getLongExtra("messageId", -1);
		if(messageId != -1) {
			message = Message.findById(Message.class, messageId);
		}
	}

	@Override
	public void onResume () {
		super.onResume();
		initialiseUi();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_queue_failed) {
			queueFailedDispatches();
		}
		if (id == R.id.action_delete_message) {
			showConfirmDeleteDialog();
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialiseUi () {
		List<Dispatch> dispatchList = message.getDispatches();
		groupNameTextView = (TextView) findViewById(R.id.currentGroupNameTextView);
		messageTextView = (TextView) findViewById(R.id.currentMessageTextView);
		dispatchListView = (ListView) findViewById(R.id.dispatchListView);
		sentCountTextView = (TextView) findViewById(R.id.sent_dispatch_count);
		failedCountTextView = (TextView) findViewById(R.id.failed_dispatch_count);
		pendingCountTextView = (TextView) findViewById(R.id.pending_dispatch_count);
		groupNameTextView.setText("To : " + message.getGroupName());
		messageTextView.setText(message.getText());
		sentCountTextView.setText(DispatchUtil.sentCount(dispatchList).toString());
		failedCountTextView.setText(DispatchUtil.failedCount(dispatchList).toString());
		pendingCountTextView.setText(DispatchUtil.pendingCount(dispatchList).toString());
		dispatchArrayAdapter =  new DispatchArrayAdapter(this, R.layout.activity_dispatch_list_item, message.getDispatches());
		dispatchListView.setAdapter(dispatchArrayAdapter);
		dispatchArrayAdapter.notifyDataSetChanged();
	}

	private void queueFailedDispatches () {
		for (Dispatch dispatch : message.getDispatches()) {
			if(dispatch.getStatus() == DispatchStatus.FAILED) {
				dispatch.setStatus(DispatchStatus.QUEUED);
				dispatch.save();
			}
		}
	}

	private void showConfirmDeleteDialog () {
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewMessageActivity.this);
		builder.setMessage(R.string.confirm_delete_message_message)
				.setTitle(R.string.confirm_delete_message_title);
		builder.setPositiveButton(R.string.delete_message, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteMessage();
			}
		});
		builder.setNegativeButton(R.string.dont_delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void deleteMessage() {
		message.delete();
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
}

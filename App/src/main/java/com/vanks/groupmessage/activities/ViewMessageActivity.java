package com.vanks.groupmessage.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.arrayadapters.view.DispatchArrayAdapter;
import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.models.persisted.Message;

/**
 * Created by vaneyck on 11/21/15.
 */
public class ViewMessageActivity extends AppCompatActivity {

	Message message;
	TextView groupNameTextView, messageTextView;
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

	private void initialiseUi () {
		groupNameTextView = (TextView) findViewById(R.id.currentGroupNameTextView);
		messageTextView = (TextView) findViewById(R.id.currentMessageTextView);
		dispatchListView = (ListView) findViewById(R.id.dispatchListView);
		groupNameTextView.setText("To : " + message.getGroupName());
		messageTextView.setText(message.getText());
		dispatchArrayAdapter =  new DispatchArrayAdapter(this, R.layout.activity_dispatch_list_item, message.getDispatches());
		dispatchListView.setAdapter(dispatchArrayAdapter);
		dispatchArrayAdapter.notifyDataSetChanged();
	}
}

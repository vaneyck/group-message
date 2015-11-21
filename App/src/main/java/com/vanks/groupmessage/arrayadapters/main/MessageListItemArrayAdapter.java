package com.vanks.groupmessage.arrayadapters.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageListItemArrayAdapter extends ArrayAdapter<Message> {
	Context context;
	int layoutResourceId;
	ArrayList<Message> messageArrayList;

	public MessageListItemArrayAdapter (Context context, int layoutResourceId, List<Message> messageList) {
		super(context, layoutResourceId, messageList);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.messageArrayList = (ArrayList<Message>) messageList;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MessageRowHolder messageRowHolder = null;
		if(row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			messageRowHolder = new MessageRowHolder();
			messageRowHolder.groupNameTextView = (TextView) row.findViewById(R.id.groupNameTextView);
			messageRowHolder.messageSummaryTextView = (TextView) row.findViewById(R.id.messageSummaryTextView);
			row.setTag(messageRowHolder);
		} else {
			messageRowHolder = (MessageRowHolder) row.getTag();
		}
		Message message = messageArrayList.get(position);
		messageRowHolder.groupNameTextView.setText(message.getGroupName());
		messageRowHolder.messageSummaryTextView.setText(message.getText());
		return row;
	}

	static class MessageRowHolder {
		TextView groupNameTextView, messageSummaryTextView;
	}
}
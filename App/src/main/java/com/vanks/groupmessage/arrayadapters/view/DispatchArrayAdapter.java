package com.vanks.groupmessage.arrayadapters.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.models.persisted.Dispatch;
import com.vanks.groupmessage.models.unsaved.Contact;
import com.vanks.groupmessage.models.unsaved.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class DispatchArrayAdapter extends ArrayAdapter<Dispatch> {
	Context context;
	int layoutResourceId;
	ArrayList<Dispatch> dispatchArrayList = new ArrayList<>();

	public DispatchArrayAdapter (Context context, int layoutResourceId, List<Dispatch> dispatchList) {
		super(context, layoutResourceId, dispatchList);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.dispatchArrayList = (ArrayList<Dispatch>) dispatchList;
	}
	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		GroupRowHolder holder = null;
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		holder = new GroupRowHolder();
		holder.contactNameTextView = (TextView) row.findViewById(R.id.contactNameTextView);
		holder.contactPhoneNumberTextView = (TextView) row.findViewById(R.id.contactPhoneNumberTextView);
		holder.dispatchStatusTextView = (TextView) row.findViewById(R.id.dispatchStatusTextView);

		Dispatch dispatch = dispatchArrayList.get(position);
		holder.contactNameTextView.setText(dispatch.getName());
		holder.contactPhoneNumberTextView.setText(dispatch.getPhoneNumber());
		holder.dispatchStatusTextView.setText(dispatch.getStatus().toString());
		return row;
	}

	static class GroupRowHolder {
		TextView contactNameTextView, contactPhoneNumberTextView, dispatchStatusTextView;
	}
}

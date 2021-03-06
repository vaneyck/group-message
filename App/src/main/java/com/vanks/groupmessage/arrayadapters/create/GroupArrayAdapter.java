package com.vanks.groupmessage.arrayadapters.create;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vanks.groupmessage.R;
import com.vanks.groupmessage.models.unsaved.Group;
import com.vanks.groupmessage.utils.GroupUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class GroupArrayAdapter extends ArrayAdapter<Group> {
	Context context;
	int layoutResourceId;
	ArrayList<Group> groupArrayList = new ArrayList<>();

	public GroupArrayAdapter (Context context, int layoutResourceId, List<Group> groupList) {
		super(context, layoutResourceId, groupList);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.groupArrayList = (ArrayList<Group>) groupList;
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
		Group group = groupArrayList.get(position);
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();

		row = inflater.inflate(layoutResourceId, parent, false);
		holder = new GroupRowHolder();
		holder.groupName = (TextView) row.findViewById(R.id.groupName);
		holder.groupId = (TextView) row.findViewById(R.id.groupId);
		holder.groupCount = (TextView) row.findViewById(R.id.groupCount);
		holder.groupName.setText(group.getName());
		holder.groupId.setText(group.getId().toString());
		holder.groupCount.setText("(" + group.getCount().toString() + ")");
		return row;
	}

	static class GroupRowHolder {
		TextView groupName, groupId, groupCount;
	}
}

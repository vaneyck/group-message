package com.vanks.groupmessage.models.persisted;

import com.orm.SugarRecord;
import com.vanks.groupmessage.enums.DispatchStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Message extends SugarRecord<Message> {
	String text;
	Long groupId;
	String groupName;
	Long dateCreated;

	public Message () {}

	public Message (String text, Long groupId, String groupName) {
		this.text = text;
		this.groupId = groupId;
		this.groupName = groupName;
		this.dateCreated = new Date().getTime();
	}

	public List<Dispatch> getDispatches (){
		String messageIdAsString = this.getId().toString();
		return Dispatch.find(Dispatch.class, "message = ?", messageIdAsString);
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName () {
		return groupName;

	}

	public Long countDispatchesByStatus (DispatchStatus status) {
		String[] args = { this.getId().toString(), status.toString() };
		return (Long) Dispatch.count(Dispatch.class, "message = ? and status = ?", args);
	}

	public Long countDispatches () {
		String[] args = { this.getId().toString() };
		return (Long) Dispatch.count(Dispatch.class, "message = ?", args);
	}

	public Long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getText () {
		return text;
	}

	public String getTextToDisplay () {
		int textSize = text.length();
		String moreHint = "";
		if(textSize > 200) {
			textSize = 200;
			moreHint = "...";
		}
		return text.substring(0, textSize) + moreHint;
	}
}

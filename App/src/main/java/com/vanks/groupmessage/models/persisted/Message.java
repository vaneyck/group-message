package com.vanks.groupmessage.models.persisted;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Message extends SugarRecord<Message> {
	String text;
	Long groupId;
	String groupName;

	public Message () {}

	public Message (String text, Long groupId, String groupName) {
		this.text = text;
		this.groupId = groupId;
		this.groupName = groupName;
	}

	/**
	 * Retrieve a list of Recipients assigned to a Message object
	 * @return a List of Recipients
	 */
	public List<Recipient> getRecipients (){
		String messageIdAsString = this.getId().toString();
		return Recipient.find(Recipient.class, "message = ?", messageIdAsString);
	}

	/**
	 * Store Recipient against a Message
	 * @param phoneNumber the Recipients phone number
	 */
	public void addRecipient (String phoneNumber) {
		Recipient recipient =  new Recipient();
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

	public String getText () {
		return text;
	}
}

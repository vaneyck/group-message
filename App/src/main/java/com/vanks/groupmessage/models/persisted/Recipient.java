package com.vanks.groupmessage.models.persisted;

import com.orm.SugarRecord;
import com.vanks.groupmessage.enums.MessageStatus;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Recipient extends SugarRecord<Recipient> {
	String phoneNumber;
	MessageStatus status;
	Message message;

	public Recipient () {}

	public Recipient (String phoneNumber, Message message) {
		this.phoneNumber = phoneNumber;
		this.status = MessageStatus.PENDING;
		this.message = message;
	}
}

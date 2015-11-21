package com.vanks.groupmessage.models.persisted;

import com.orm.SugarRecord;
import com.vanks.groupmessage.enums.DispatchStatus;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Dispatch extends SugarRecord<Dispatch> {
	String phoneNumber;
	String name;
	DispatchStatus status;
	Message message;
	Long dateSent;
	boolean attempted = false;

	public Long getDateSent() {
		return dateSent;
	}

	public void setDateSent(Long dateSent) {
		this.dateSent = dateSent;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public DispatchStatus getStatus() {
		return status;
	}

	public void setStatus(DispatchStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Dispatch() {}

	public Dispatch(String phoneNumber, String name, Message message) {
		this.phoneNumber = phoneNumber;
		this.status = DispatchStatus.PENDING;
		this.message = message;
		this.name = name;
	}

	public boolean isAttempted() {
		return attempted;
	}

	public void setAttempted(boolean attempted) {
		this.attempted = attempted;
	}
}

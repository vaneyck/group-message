package com.vanks.groupmessage.models;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Contact {
	String name;
	String phoneNumber;

	public Contact (String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
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
}

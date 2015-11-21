package com.vanks.groupmessage.models;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Group {
	String name;
	Long id;

	public Group (String name, Long id) {
		this.name = name;
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

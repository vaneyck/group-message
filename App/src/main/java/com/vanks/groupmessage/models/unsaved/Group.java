package com.vanks.groupmessage.models.unsaved;

/**
 * Created by vaneyck on 11/21/15.
 */
public class Group {
	String name;
	Long id;
	Long count;

	public Group (String name, Long id, Long count) {
		this.name = name;
		this.id = id;
		this.count = count;
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

	public Long getCount () {
		return this.count;
	}
}

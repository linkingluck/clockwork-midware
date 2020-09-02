package com.linkingluck.midware.resource.excel;

import com.linkingluck.midware.resource.anno.Resource;
import com.linkingluck.midware.resource.anno.ResourceId;

@Resource(suffix = "xlsx")
public class RandomNameResource {

	@ResourceId
	private int id;

	private String firstName;

	private String lastName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}

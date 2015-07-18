package org.keyser.anr.core;

public class UserDragActionTo {

	private String action;

	private Object value;

	private CardLocation location;

	public UserDragActionTo(String action, Object value, CardLocation location) {
		this.action = action;
		this.value = value;
		this.location = location;
	}

	public String getAction() {
		return action;
	}

	public Object getValue() {
		return value;
	}

	public CardLocation getLocation() {
		return location;
	}
}

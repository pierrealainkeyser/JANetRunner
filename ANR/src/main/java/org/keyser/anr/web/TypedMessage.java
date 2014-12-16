package org.keyser.anr.web;

import java.io.Serializable;

public class TypedMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 900954751057848783L;

	private Object data;

	private String type;

	public TypedMessage() {
	}

	public TypedMessage(String type, Object data) {
		this.type = type;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public String getType() {
		return type;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "OutputMessage [data=" + data + ", type=" + type + "]";
	}
}

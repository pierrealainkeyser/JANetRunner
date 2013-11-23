package org.keyser.anr.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class Notification {
	private final String type;

	private final Map<String, Object> args;

	public Notification(String type) {
		this.type = type;
		this.args = new LinkedHashMap<>();
	}

	public Notification m(String key, Object value) {
		args.put(key, value);
		return this;
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

}

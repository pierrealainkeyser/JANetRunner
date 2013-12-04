package org.keyser.anr.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class Notification {
	private final Map<NotificationAttribute, Object> args;

	private final NotificationEvent type;

	public Notification(NotificationEvent type) {
		this.type = type;
		this.args = new LinkedHashMap<>();
	}

	public Map<NotificationAttribute, Object> getArgs() {
		return args;
	}

	public NotificationEvent getType() {
		return type;
	}

	public Notification m(Card c) {
		return m(NotificationAttribute.CARD, c);
	}

	public Notification m(GameStep step) {
		return m(NotificationAttribute.STEP, step);
	}

	private Notification m(NotificationAttribute key, Object value) {
		args.put(key, value);
		return this;
	}

	public Notification m(Run run) {
		return m(NotificationAttribute.RUN, run);
	}

	public Notification m(WalletUnit w) {
		return m(NotificationAttribute.WALLET, w);
	}

	@Override
	public String toString() {
		return "Notification [type=" + type + ", args=" + args + "]";
	}

	public Notification m(WinCondition result) {
		return m(NotificationAttribute.RESULT, result);
	}

}

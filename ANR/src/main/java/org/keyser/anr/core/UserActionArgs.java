package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class UserActionArgs<T> extends UserAction {

	private final T data;

	public UserActionArgs(AbstractId user, AbstractCard source, CostForAction cost, String description, T data) {
		super(user, source, cost, description);
		this.data = data;
	}

	public UserActionArgs(AbstractId user, CorpServer source, CostForAction cost, String description, T data) {
		super(user, source, cost, description);
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return ((Class<T>) data.getClass());
	}

	public T getData() {
		return data;
	}
}

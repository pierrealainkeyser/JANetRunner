package org.keyser.anr.core;

public class UserActionWithArgs<T> extends UserAction {

	private final Class<T> type;

	public UserActionWithArgs(AbstractCard source, CostForAction cost, String description, Class<T> type) {
		super(source, cost, description);
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

}

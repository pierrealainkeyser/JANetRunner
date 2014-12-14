package org.keyser.anr.core;

public class UserActionWithArgs<T> extends UserAction {

	private final Class<T> type;

	public UserActionWithArgs(AbstractId to, AbstractCard source, CostForAction cost, String description, Class<T> type) {
		super(to, source, cost, description);
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

}

package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class UserActionArgs<T> extends UserAction {

	private final Class<T> type;

	public UserActionArgs(AbstractId user, AbstractCard source, CostForAction cost, String description, Class<T> type) {
		super(user, source, cost, description);
		this.type = type;
	}

	public UserActionArgs(AbstractId user, CorpServer source, CostForAction cost, String description, Class<T> type) {
		super(user, source, cost, description);
		this.type = type;
	}

	public UserActionArgs(AbstractId user, AbstractCard source, CostForAction cost, String description, Class<T> type, Object data) {
		super(user, source, cost, description, data);
		this.type = type;
	}

	public UserActionArgs(AbstractId user, CorpServer source, CostForAction cost, String description, Class<T> type, Object data) {
		super(user, source, cost, description, data);
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	public FeedbackWithArgs<UserActionArgs<T>, T> spendAndApplyArg(BiEventConsumer<UserActionArgs<T>, T> consumer) {
		return new FeedbackWithArgs<>(this, (action, input, next) -> {
			to.spend(getCost(), () -> consumer.apply(action, input, next));
		});
	}
	
	public FeedbackWithArgs<UserActionArgs<T>, T> applyArg(EventConsumer<T> consumer) {
		return new FeedbackWithArgs<>(this, (action, input, next) -> {
			consumer.apply(input,next);
		});
	}


	public FeedbackWithArgs<UserActionArgs<T>, T> spendAndApplyArg(EventConsumer<T> consumer) {
		return new FeedbackWithArgs<>(this, (action, input, next) -> {
			to.spend(getCost(), () -> consumer.apply(input, next));
		});
	}

}

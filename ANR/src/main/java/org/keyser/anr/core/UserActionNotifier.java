package org.keyser.anr.core;

import java.util.HashMap;
import java.util.Map;

public class UserActionNotifier {

	private class ActionsContext {

		private Map<Integer, UserActionHandler<?>> actions = new HashMap<>();

		private UserActionContext context;
	}

	public class UserActionHandler<T> {

		private final FlowArg<T> consumer;

		private final Class<T> type;

		private final UserAction userAction;

		private UserActionHandler(UserAction userAction, Class<T> type,
				FlowArg<T> consumer) {
			this.type = type;
			this.consumer = consumer;
			this.userAction = userAction;
		}

		public void apply(Object o) {
			T t = convert(type, o);
			consumer.apply(t);
		}

		public UserAction getUserAction() {
			return userAction;
		}
	}

	private ActionsContext actionsContext;

	private int nextAction;

	public <T, UA extends UserActionWithArgs<T>> void add(UA ua, Flow next,
			BiEventConsumer<UA, T> consumer) {

		int id = nextAction();
		ua.setActionId(id);
		actionsContext.actions.put(id,
				new UserActionHandler<T>(ua, ua.getType(), o -> {
					consumer.apply(ua, o, next);
				}));

	}

	public <UA extends UserAction> void add(UA ua, Flow next,
			EventConsumer<UA> consumer) {

		int id = nextAction();
		ua.setActionId(id);
		actionsContext.actions.put(id, new UserActionHandler<Object>(ua, null,
				o -> {
					consumer.apply(ua, next);
				}));
	}

	private <T> T convert(Class<T> type, Object response) {
		if (type == null || response == null)
			return null;

		// TODO gestion de la conversion
		return type.cast(response);
	}

	public void invoke(int actionId) {
		invoke(actionId, null);
	}

	@SuppressWarnings("unchecked")
	public void invoke(int actionId, Object response) {
		UserActionHandler<?> uah = actionsContext.actions.get(actionId);

		// nouveau conteneur d'action
		actionsContext = new ActionsContext();
		uah.apply(response);
	}

	private int nextAction() {
		return nextAction++;
	}

}

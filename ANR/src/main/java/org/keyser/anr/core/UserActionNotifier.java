package org.keyser.anr.core;

import java.util.HashMap;
import java.util.Map;

public class UserActionNotifier {

	private int nextAction;

	private ActionsContext actionsContext;

	private class ActionsContext {

		private UserActionContext context;

		private Map<Integer, UserAction<?>> actions = new HashMap<>();
	}

	public int nextAction() {
		return nextAction++;
	}

	public void add(UserAction<?> ua) {
		actionsContext.actions.put(ua.getActionId(), ua);
	}

	private Object convert(Class<?> type, Object response) {
		if (type == null || response == null)
			return null;

		// TODO gestion de la conversion

		return response;
	}

	@SuppressWarnings("unchecked")
	public void invoke(int actionId, Object response) {
		UserAction<Object> ua = (UserAction<Object>) actionsContext.actions
				.get(actionId);

		// nouveau conteneur d'action
		actionsContext = new ActionsContext();
		Class<Object> inputType = ua.getInputType();
		if (inputType != null)
			ua.apply(convert(inputType, response));
		else
			ua.apply();
	}

	public void invoke(int actionId) {
		invoke(actionId, null);
	}

}

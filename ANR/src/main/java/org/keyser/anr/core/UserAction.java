package org.keyser.anr.core;

public class UserAction {

	private int actionId;

	private final AbstractCard source;

	private final Cost cost;

	private final String description;

	public UserAction(AbstractCard source, Cost cost,
			String description) {
		this.source = source;
		this.cost = cost;
		this.description = description;
	}

	public int getActionId() {
		return actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public AbstractCard getSource() {
		return source;
	}

	public Cost getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}


}

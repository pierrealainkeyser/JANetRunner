package org.keyser.anr.core;

public abstract class UserAction<T> {

	private int actionId;

	private final AbstractCard source;

	protected final Flow next;

	private final Class<T> inputType;

	private final Cost cost;

	private final String description;

	public UserAction(Flow next, AbstractCard source, Cost cost,
			String description) {
		this(next, source, cost, description, null);

	}

	public UserAction(Flow next, AbstractCard source, Cost cost,
			String description, Class<T> inputType) {
		this.next = next;
		this.source = source;
		this.inputType = inputType;
		this.cost = cost;
		this.description = description;
	}

	public void apply(T t) {
		apply();
	}

	protected void done() {
		next.apply();
	}

	public void apply() {
		done();
	}

	public int getActionId() {
		return actionId;
	}

	public AbstractCard getSource() {
		return source;
	}

	public Class<T> getInputType() {
		return inputType;
	}

	public Cost getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

}

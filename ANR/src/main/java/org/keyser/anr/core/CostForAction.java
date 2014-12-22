package org.keyser.anr.core;

public class CostForAction {

	private final Cost cost;

	private final Object action;

	public CostForAction(Cost cost, Object action) {
		super();
		this.cost = cost;
		this.action = action;
	}

	@Override
	public String toString() {
		return "CostForAction [" + cost + ", " + action + "]";
	}

	public Cost getCost() {
		return cost;
	}

	public Object getAction() {
		return action;
	}

	/**
	 * Permet de dupliquer le cout
	 * 
	 * @param newCost
	 * @return
	 */
	public CostForAction merge(Cost newCost) {
		return new CostForAction(newCost, action);
	}
}

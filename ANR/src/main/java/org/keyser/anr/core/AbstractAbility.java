package org.keyser.anr.core;

/**
 * 
 * 
 * @author PAF
 * 
 */
public abstract class AbstractAbility {

	private final Cost cost;

	private final String name;

	private final Object action;

	protected AbstractAbility(String name, Cost cost, Object action) {
		this.name = name;
		this.cost = cost;
		this.action = action;
	}

	public Cost getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public boolean isAffordable(Wallet wallet) {
		return wallet.isAffordable(cost, action);
	}

	public int timesAffordable(Wallet wallet, int max) {
		return wallet.timesAffordable(cost, action, max);
	}

	public boolean isEnabled() {
		return true;
	}

	public Object getAction() {
		return action;
	}
}

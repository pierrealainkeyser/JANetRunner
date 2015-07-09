package org.keyser.anr.core;

public class VariableCost {
	private final Cost cost;

	private final boolean enabled;

	public VariableCost(Cost cost, boolean enabled) {
		this.cost = cost;
		this.enabled = enabled;
	}

	public Cost getCost() {
		return cost;
	}

	public boolean isEnabled() {
		return enabled;
	}
}

package org.keyser.anr.core;

public final class CostElement {

	private final int value;

	private final CostType type;

	public CostElement(int value, CostType type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public String toString() {
		return value + "-" + type;
	}

	public int getValue() {
		return value;
	}

	public CostType getType() {
		return type;
	}
}

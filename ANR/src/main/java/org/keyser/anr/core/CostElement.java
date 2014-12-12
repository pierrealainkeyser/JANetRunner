package org.keyser.anr.core;

public final class CostElement {

	public enum Type {
		CREDIT, ACTION, TRASH_SELF
	}

	private final int value;

	private final Type type;

	public CostElement(int value, Type type) {
		this.value = value;
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public Type getType() {
		return type;
	}
}

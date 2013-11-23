package org.keyser.anr.core;

public abstract class CostDeterminationEvent extends Event {

	private final Cost original;

	private Cost effective;

	protected CostDeterminationEvent(Cost original) {
		this.original = original;
		this.effective = original.clone();
	}

	public Cost getOriginal() {
		return original;
	}

	public Cost getEffective() {
		return effective;
	}
}

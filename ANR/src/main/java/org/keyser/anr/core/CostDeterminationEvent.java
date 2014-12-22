package org.keyser.anr.core;

import java.util.function.Predicate;

public class CostDeterminationEvent implements SequentialEvent {

	private final CostForAction original;

	private Cost effective;

	public CostDeterminationEvent(CostForAction original) {
		this.original = original;
		this.effective = original.getCost().clone();
	}	

	public static Predicate<CostDeterminationEvent> with(Predicate<Object> pred) {
		return (c) -> pred.test(c.original.getAction());
	}

	public CostForAction merged() {
		return original.merge(effective);
	}

	public CostForAction getOriginal() {
		return original;
	}

	public Cost getEffective() {
		return effective;
	}

	@Override
	public String toString() {
		return "CostDeterminationEvent [original=" + original + ", effective=" + effective + "]";
	}
}

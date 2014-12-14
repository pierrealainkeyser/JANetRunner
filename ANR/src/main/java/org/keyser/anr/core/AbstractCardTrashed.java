package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardTrashed implements SequentialEvent {

	private final AbstractCard thrashed;

	public AbstractCardTrashed(AbstractCard thrashed) {
		super();
		this.thrashed = thrashed;
	}

	public AbstractCard getThrashed() {
		return thrashed;
	}

	public static Predicate<AbstractCardTrashed> with(Predicate<AbstractCard> pred) {
		return (c) -> pred.test(c.thrashed);
	}
}

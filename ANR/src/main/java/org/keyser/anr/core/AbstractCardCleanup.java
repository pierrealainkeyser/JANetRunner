package org.keyser.anr.core;

public class AbstractCardCleanup implements SequentialEvent {

	protected final AbstractCard card;

	public static Predicate<? extends AbstractCardCleanup> with(Predicate<AbstractCard> pred) {
		return (c) -> pred.test(c.card);
	}

	public AbstractCardCleanup(AbstractCard card) {
		this.card=card;
	}

}
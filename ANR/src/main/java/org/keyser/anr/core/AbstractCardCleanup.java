package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardCleanup implements SequentialEvent {

	protected final AbstractCard card;

	public static <T extends AbstractCardCleanup> Predicate<T> with(Predicate<AbstractCard> pred) {
		return (c) -> pred.test(c.card);
	}

	public AbstractCardCleanup(AbstractCard card) {
		this.card = card;
	}

}
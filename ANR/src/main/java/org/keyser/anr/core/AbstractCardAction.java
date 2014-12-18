package org.keyser.anr.core;

public class AbstractCardAction<T extends AbstractCard> {

	protected final T card;

	public AbstractCardAction(T card) {
		this.card = card;
	}

	public T getCard() {
		return card;
	}

}
package org.keyser.anr.core;

public class PlayAbstractCardAction<T extends AbstractCard> {
	
	private final T card;

	protected PlayAbstractCardAction(T card) {
		this.card = card;
	}

	public T getCard() {
		return card;
	}

}

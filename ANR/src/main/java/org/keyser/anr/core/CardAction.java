package org.keyser.anr.core;

public abstract class CardAction {

	private final Card card;

	public CardAction(Card card) {
		this.card = card;
	}

	public Card getCard() {
		return card;
	}
}

package org.keyser.anr.core;

public class CardTrashedEvent extends Event implements CardAccess {

	private final Card card;

	public CardTrashedEvent(Card card) {
		this.card = card;
	}

	@Override
	public Card getCard() {
		return card;
	}
}

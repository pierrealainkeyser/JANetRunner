package org.keyser.anr.core;

public class CardTrashedEvent extends Event {

	private final Card card;

	public CardTrashedEvent(Card card) {
		this.card = card;
	}

	public Card getCard() {
		return card;
	}
}

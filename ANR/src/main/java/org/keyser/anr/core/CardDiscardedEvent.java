package org.keyser.anr.core;

public class CardDiscardedEvent extends Event {

	public Card getDiscarded() {
		return discarded;
	}

	private final Card discarded;

	public CardDiscardedEvent(Card discarded) {
		this.discarded = discarded;
	}
}

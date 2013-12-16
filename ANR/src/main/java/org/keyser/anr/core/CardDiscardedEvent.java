package org.keyser.anr.core;

public class CardDiscardedEvent extends Event {

	private final Card discarded;

	public CardDiscardedEvent(Card discarded) {
		this.discarded = discarded;
	}

	public Card getDiscarded() {
		return discarded;
	}
}

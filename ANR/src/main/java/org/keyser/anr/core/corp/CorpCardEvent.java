package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

public abstract class CorpCardEvent extends Event {

	private final CorpCard card;

	public CorpCardEvent(CorpCard card) {
		this.card = card;
	}

	public CorpCard getCard() {
		return card;
	}

}
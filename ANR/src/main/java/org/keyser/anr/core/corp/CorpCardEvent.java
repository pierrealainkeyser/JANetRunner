package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardAccess;
import org.keyser.anr.core.Event;

public abstract class CorpCardEvent extends Event implements CardAccess {

	private final CorpCard card;

	public CorpCardEvent(CorpCard card) {
		this.card = card;
	}

	@Override
	public CorpCard getCard() {
		return card;
	}

}
package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

/**
 * L'evenement la Corp a piocher
 * 
 * @author PAF
 * 
 */
public class CorpCardDrawn extends Event {
	private final CorpCard card;

	public CorpCardDrawn(CorpCard card) {
		this.card = card;
	}

	public CorpCard getCard() {
		return card;
	}
}
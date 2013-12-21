package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

/**
 * L'evenement la Corp a piocher
 * 
 * @author PAF
 * 
 */
public class CorpCardDraw extends Event {
	private final CorpCard card;

	public CorpCardDraw(CorpCard card) {
		this.card = card;
	}

	public CorpCard getCard() {
		return card;
	}
}
package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardAction;

public class ThrashCardAction extends CardAction {

	public ThrashCardAction(CorpCard card) {
		super(card);
	}

	@Override
	public CorpCard getCard() {
		return (CorpCard) super.getCard();
	}
}

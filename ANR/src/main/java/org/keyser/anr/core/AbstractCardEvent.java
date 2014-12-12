package org.keyser.anr.core;

public abstract class AbstractCardEvent implements SequentialEvent {

	private final AbstractCard card;

	protected AbstractCardEvent(AbstractCard card) {
		this.card = card;
	}

	public AbstractCard getCard() {
		return card;
	}
}

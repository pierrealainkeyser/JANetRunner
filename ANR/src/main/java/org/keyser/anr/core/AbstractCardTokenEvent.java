package org.keyser.anr.core;

public class AbstractCardTokenEvent extends AbstractCardEvent {

	private final TokenType type;

	public AbstractCardTokenEvent(AbstractCard card, TokenType type) {
		super(card, null, null);
		this.type = type;
	}

	public TokenType getType() {
		return type;
	}
}

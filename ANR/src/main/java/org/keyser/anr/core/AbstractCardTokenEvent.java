package org.keyser.anr.core;

public class AbstractCardTokenEvent extends AbstractCardMonitoringEvent {

	private final TokenType type;

	public AbstractCardTokenEvent(AbstractCard card, TokenType type) {
		super(card);
		this.type = type;
	}

	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "AbstractCardTokenEvent [" + getType() + ", " + getPrimary() + "]";
	}
}

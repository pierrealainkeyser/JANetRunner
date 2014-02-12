package org.keyser.anr.core;

public class CardLocationHosted extends CardLocation {

	private final Card host;

	public CardLocationHosted(Card host) {
		super(Where.HOSTED);
		this.host = host;
	}

	public Card getHost() {
		return host;
	}

}

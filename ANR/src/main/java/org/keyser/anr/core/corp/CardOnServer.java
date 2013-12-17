package org.keyser.anr.core.corp;

public class CardOnServer {

	private final CorpCard card;

	private final CorpServer server;

	public CardOnServer(CorpCard card, CorpServer server) {
		this.card = card;
		this.server = server;
	}

	public CorpCard getCard() {
		return card;
	}

	public CorpServer getServer() {
		return server;
	}
}

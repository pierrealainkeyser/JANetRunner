package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public abstract class CardLocationOnServer extends CardLocation {

	private final CorpServer server;

	private final int index;

	public CardLocationOnServer(Where where, CorpServer server, int index) {
		super(where);
		this.server = server;
		this.index = index;
	}

	public CorpServer getServer() {
		return server;
	}

	public int getIndex() {
		return index;
	}
}

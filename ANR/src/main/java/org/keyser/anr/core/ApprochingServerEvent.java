package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class ApprochingServerEvent {

	private final CorpServer server;

	public ApprochingServerEvent(CorpServer server) {
		this.server = server;
	}

	public CorpServer getServer() {
		return server;
	}
}

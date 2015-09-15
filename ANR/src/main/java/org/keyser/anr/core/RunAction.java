package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class RunAction {

	private final CorpServer server;

	public RunAction(CorpServer server) {
		this.server = server;
	}

	public CorpServer getServer() {
		return server;
	}

}

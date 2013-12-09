package org.keyser.anr.core.corp;

public class InstallIceAction {

	private final Ice ice;

	private final CorpServer server;

	public InstallIceAction(Ice ice, CorpServer server) {
		this.ice = ice;
		this.server = server;
	}

	public Ice getIce() {
		return ice;
	}

	public CorpServer getServer() {
		return server;
	}
}

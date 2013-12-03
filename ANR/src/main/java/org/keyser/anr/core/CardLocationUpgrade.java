package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class CardLocationUpgrade extends CardLocationOnServer {

	public CardLocationUpgrade(CorpServer server, int index) {
		super(Where.UPGRADE, server, index);
	}

}

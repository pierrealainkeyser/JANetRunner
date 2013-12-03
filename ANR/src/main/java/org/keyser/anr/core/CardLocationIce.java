package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class CardLocationIce extends CardLocationOnServer {

	public CardLocationIce(CorpServer server, int index) {
		super(Where.ICE, server, index);
	}

}

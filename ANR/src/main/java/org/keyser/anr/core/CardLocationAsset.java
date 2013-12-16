package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class CardLocationAsset extends CardLocationOnServer {

	public CardLocationAsset(CorpServer server, int index) {
		super(Where.ASSET, server, index);
	}

}

package org.keyser.anr.core;

import java.util.Stack;

import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.Ice;

public class CardLocationIce extends CardLocationOnServer {

	public CardLocationIce(CorpServer server, int index) {
		super(Where.ICE, server, index);
	}

	public Stack<Ice> ices() {
		return getServer().getIces();
	}

}

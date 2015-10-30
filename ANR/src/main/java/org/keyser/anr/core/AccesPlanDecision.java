package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

/**
 * L'evenement pour accéder à un server
 * 
 * @author PAF
 *
 */
public class AccesPlanDecision {

	private final CorpServer corpServer;

	private int inStack = 1;

	public AccesPlanDecision(CorpServer corpServer) {
		this.corpServer = corpServer;
	}

	public CorpServer getCorpServer() {
		return corpServer;
	}

	public void increaseInStack(int delta) {
		this.inStack += delta;
	}

	public int getInStack() {
		return inStack;
	}

	public void setInStack(int inStack) {
		this.inStack = inStack;
	}

}

package org.keyser.anr.core;

public abstract class Event extends AbstractGameContent {

	private boolean prevented;

	public void prevents() {
		prevented = true;
	}

	public boolean isPrevented() {
		return prevented;
	}

}

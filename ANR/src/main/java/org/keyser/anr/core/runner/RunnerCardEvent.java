package org.keyser.anr.core.runner;

import org.keyser.anr.core.Event;

public abstract class RunnerCardEvent extends Event {

	private final RunnerCard card;

	RunnerCardEvent(RunnerCard card) {
		this.card = card;
	}

	public RunnerCard getCard() {
		return card;
	}

}
package org.keyser.anr.core.runner;

import org.keyser.anr.core.CardAccess;
import org.keyser.anr.core.Event;

public abstract class RunnerCardEvent extends Event implements CardAccess{

	private final RunnerCard card;

	RunnerCardEvent(RunnerCard card) {
		this.card = card;
	}

	@Override
	public RunnerCard getCard() {
		return card;
	}

}
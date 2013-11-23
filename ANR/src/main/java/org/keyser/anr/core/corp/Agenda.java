package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Agenda extends InstallableCorpCard {

	private final int score;

	private final int requirement;

	public Agenda(Influence influence, int score, int requirement) {
		super(influence, Cost.free());
		this.score = score;
		this.requirement = requirement;
	}

	public int getScore() {
		return score;
	}

	public int getRequirement() {
		return requirement;
	}

}

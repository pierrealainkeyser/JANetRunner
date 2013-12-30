package org.keyser.anr.core;

public enum GameStep {
	CORP_ACT, CORP_DISCARD, CORP_DRAW, RUNNER_ACT, RUNNER_DISCARD, RUNNING;

	public boolean isTurn(Player p) {
		if (RUNNER_ACT == this)
			return Player.RUNNER == p;
		else if (CORP_ACT==this)
			return Player.CORP == p;
		return false;

	}

	public boolean mayPlayAction() {
		return RUNNER_ACT == this || CORP_ACT == this;
	}

	public boolean mayRezzIce() {
		return RUNNING == this;
	}

	public boolean mayScoreAgenda() {
		return CORP_DRAW == this || CORP_ACT == this;
	}
}
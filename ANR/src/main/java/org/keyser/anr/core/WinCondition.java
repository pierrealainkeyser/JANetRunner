package org.keyser.anr.core;

public enum WinCondition {
	CORP_BUST, CORP_SCORED, FLATLINE, RUNNER_SCORED;

	public boolean isCorpVictory() {
		return FLATLINE == this || CORP_SCORED == this;
	}

	public boolean isRunnerVictory() {
		return CORP_BUST == this || RUNNER_SCORED == this;
	}
}
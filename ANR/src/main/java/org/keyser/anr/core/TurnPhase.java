package org.keyser.anr.core;

public enum TurnPhase {
	ACTION_WILL_START("Actions phase will begin"), ACTION("Actions phase"), ACTION_WILL_END("Actions phase will end"), DISCARD("Discarding phase"), DRAW("Drawing phase"), INITING("Initing phase"), RUNNING(
			"Running phase");

	private String text;

	private TurnPhase(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
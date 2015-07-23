package org.keyser.anr.core;

public enum TurnPhase {
	ACTION_WILL_START("Acting phase will begin"), ACTION("Acting phase"), ACTION_WILL_END("Acting phase will end"), DISCARD("Discarding phase"), DRAW("Drawing phase"), INITING("Initing phase"), RUNNING(
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
package org.keyser.anr.core;

public enum PlayerType {
	CORP, RUNNER;

	public PlayerType next() {
		return CORP == this ? RUNNER : CORP;
	}
}

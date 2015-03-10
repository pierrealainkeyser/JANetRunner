package org.keyser.anr.core;

public enum TrashCause {
	PLAY, EXHAUSTED;

	public boolean isRezzedAfterTrash() {
		return true;
	}

}

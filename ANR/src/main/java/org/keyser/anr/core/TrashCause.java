package org.keyser.anr.core;

public enum TrashCause {

	PLAY(true), REZZ(false), OTHER_INSTALLED(false), EXHAUSTED(true);

	private final boolean rezzedAfterTrash;

	private TrashCause(boolean rezzedAfterTrash) {
		this.rezzedAfterTrash = rezzedAfterTrash;
	}

	public boolean isRezzedAfterTrash() {
		return rezzedAfterTrash;
	}

}

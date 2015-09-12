package org.keyser.anr.core;

public enum TrashCause {

	ON_ACCESS(true),PLAY(true),MEMORY_CHECK(true), REZZ(false), OTHER_INSTALLED(false), EXHAUSTED(true), DAMAGE(true), DISCARD(false);

	private final boolean rezzedAfterTrash;

	private TrashCause(boolean rezzedAfterTrash) {
		this.rezzedAfterTrash = rezzedAfterTrash;
	}

	public boolean isRezzedAfterTrash() {
		return rezzedAfterTrash;
	}

}

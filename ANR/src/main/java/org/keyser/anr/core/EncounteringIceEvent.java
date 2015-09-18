package org.keyser.anr.core;

public class EncounteringIceEvent {

	private final EncounteredIce encontered;

	public EncounteringIceEvent(EncounteredIce encontered) {
		this.encontered = encontered;
	}

	public EncounteredIce getEncontered() {
		return encontered;
	}

	public boolean isBypassed() {
		return encontered.isBypassed();
	}
}

package org.keyser.anr.core;

public class Clicks {
	private int active;

	private int used;

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	public int remaining() {
		return active - used;
	}
}

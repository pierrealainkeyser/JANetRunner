package org.keyser.anr.core;

public class Clicks {
	private int active;

	private int used;

	public Clicks duplicate() {
		Clicks c = new Clicks();
		c.setActive(getActive());
		c.setUsed(getUsed());
		return c;
	}

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

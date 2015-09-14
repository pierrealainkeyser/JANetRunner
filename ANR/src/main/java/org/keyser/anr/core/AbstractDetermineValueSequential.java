package org.keyser.anr.core;


public abstract class AbstractDetermineValueSequential extends AbstractCardEvent implements SequentialEvent {

	private int base;
	private int delta;

	protected AbstractDetermineValueSequential(AbstractCard primary, int base) {
		super(primary, null);
		this.base = base;
	}

	public int getComputed() {
		return base + delta;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

}
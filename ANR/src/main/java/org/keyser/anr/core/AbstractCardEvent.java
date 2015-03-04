package org.keyser.anr.core;

public abstract class AbstractCardEvent {

	private AbstractCard primary;

	private AbstractCard secondary;

	protected AbstractCardEvent(AbstractCard primary, AbstractCard secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	public Game getGame() {
		return primary.getGame();
	}

	public AbstractCard getPrimary() {
		return primary;
	}

	public AbstractCard getSecondary() {
		return secondary;
	}
}

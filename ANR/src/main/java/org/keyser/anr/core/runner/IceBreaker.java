package org.keyser.anr.core.runner;

import org.keyser.anr.core.TokenType;

public abstract class IceBreaker extends Program {

	protected IceBreaker(int id, IceBreakerMetaCard meta) {
		super(id, meta);
	}

	@Override
	protected IceBreakerMetaCard getMeta() {
		return (IceBreakerMetaCard) super.getMeta();
	}

	/**
	 * Place le nombre de token qui va bien
	 */
	public void computeStrength() {
		DetermineIceBreakerStrengthEvent evt = new DetermineIceBreakerStrengthEvent(this);
		game.fire(evt);
		int cpt = evt.computeStrength();
		int strength = getStrength();
		setToken(TokenType.STRENGTH, Math.max(cpt - strength, 0));
	}

	public int getComputedStrength() {
		return getToken(TokenType.STRENGTH) + getStrength();
	}

	public int getStrength() {
		return getMeta().getStrength();
	}
}

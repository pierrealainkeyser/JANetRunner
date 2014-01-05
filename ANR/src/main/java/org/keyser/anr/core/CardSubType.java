package org.keyser.anr.core;

public enum CardSubType {

	// pour les icebreakers
	IA, FRACTER, KILLER, DECODER,

	// pour les ices
	BARRIER, SENTRY, CODEGATE, TRAP,

	// pour des pieges
	AMBUSH;

	public boolean isIceBreaker() {
		return IA == this || FRACTER == this || KILLER == this || DECODER == this;
	}

	public boolean isIce() {
		return BARRIER == this || SENTRY == this || CODEGATE == this || TRAP == this;
	}

	/**
	 * Renvoi vrai si le type d'icebreaker peut casser le type d'ice
	 * 
	 * @param c
	 * @return
	 */
	public boolean mayBreak(CardSubType c) {
		if (isIceBreaker()) {

			if (IA == this)
				return true;
			else if (FRACTER == this && BARRIER == c)
				return true;
			else if (KILLER == this && SENTRY == c)
				return true;
			else if (DECODER == this && CODEGATE == c)
				return true;

		} else if (isIce()) {
			return c.mayBreak(this);
		}

		return false;
	}
}

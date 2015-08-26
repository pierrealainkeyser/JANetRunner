package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardEvent;
import org.keyser.anr.core.SequentialEvent;

/**
 * Permet de connaitre la taille maximum de la main
 * 
 * @author pakeyser
 *
 */
public class DetermineIceBreakerStrengthEvent extends AbstractCardEvent implements SequentialEvent {

	private int strength;

	private int delta;

	public DetermineIceBreakerStrengthEvent(IceBreaker iceBreaker) {
		super(iceBreaker, null);
		this.strength = iceBreaker.getStrength();
	}

	public int computeStrength() {
		return strength + delta;
	}

	@Override
	public IceBreaker getPrimary() {
		return (IceBreaker) super.getPrimary();
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

}

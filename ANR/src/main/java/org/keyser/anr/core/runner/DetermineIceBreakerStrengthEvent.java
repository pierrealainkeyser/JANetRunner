package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractDetermineValueSequential;


/**
 * Permet de connaitre la taille maximum de la main
 * 
 * @author pakeyser
 *
 */
public class DetermineIceBreakerStrengthEvent extends AbstractDetermineValueSequential {

	

	public DetermineIceBreakerStrengthEvent(IceBreaker iceBreaker) {
		super(iceBreaker, iceBreaker.getBaseStrength());
	}

	
	@Override
	public IceBreaker getPrimary() {
		return (IceBreaker) super.getPrimary();
	}

	

}

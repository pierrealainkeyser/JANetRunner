package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractDetermineValueSequential;


/**
 * Permet de connaitre la taille maximum de la main
 * 
 * @author pakeyser
 *
 */
public class DetermineIceStrengthEvent extends AbstractDetermineValueSequential {

	

	public DetermineIceStrengthEvent(Ice ice) {
		super(ice, ice.getBaseStrength());
	}

	
	@Override
	public Ice getPrimary() {
		return (Ice) super.getPrimary();
	}

	

}

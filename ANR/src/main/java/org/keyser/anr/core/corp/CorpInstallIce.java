package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

/**
 * L'evenement la Corp a piocher
 * 
 * @author PAF
 * 
 */
public class CorpInstallIce extends Event {
	private final Ice ice;

	public CorpInstallIce(Ice ice) {
		this.ice = ice;
	}

	public Ice getIce() {
		return ice;
	}
}
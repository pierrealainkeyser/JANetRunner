package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.ReadyedRoutine;
import org.keyser.anr.core.corp.Routine;

/**
 * L'état de la glace
 * 
 * @author PAF
 * 
 */
public class EncounteredIce {

	private List<ReadyedRoutine> routines = new ArrayList<>();

	private boolean bypassed;
	private final Ice ice;

	public EncounteredIce(Ice ice) {
		this.ice = ice;

		ice.getRoutines().forEach(this::addRoutine);
	}

	private ReadyedRoutine ready(Routine r) {
		return ice.getGame().createRoutine(r);
	}

	/**
	 * Rajoute une rjoute
	 * 
	 * @param r
	 */
	public void addRoutine(Routine r) {
		routines.add(ready(r));
	}

	public boolean isRezzed() {
		return ice.isRezzed();
	}

	public boolean isBypassed() {
		return bypassed;
	}

	public void setBypassed(boolean bypassed) {
		this.bypassed = bypassed;
	}

	public Ice getIce() {
		return ice;
	}

	public List<ReadyedRoutine> getRoutines() {
		return Collections.unmodifiableList(routines);
	}
}
package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.Routine;

/**
 * L'état de la glace
 * 
 * @author PAF
 * 
 */
public class EncounteredIce {

	private List<Routine> toBeBrokens = new ArrayList<>();
	private List<Routine> brokens = new ArrayList<>();
	private boolean bypassed;
	private final Ice ice;

	public EncounteredIce(Ice ice) {
		this.ice = ice;
		toBeBrokens.addAll(ice.getRoutines());
	}

	public void addBroken(Routine r) {
		brokens.add(r);
	}

	public int countBrokens() {
		return brokens.size();
	}

	public int countUnbrokens() {
		return toBeBrokens.size();
	}

	public boolean isRezzed() {
		return ice.isRezzed();
	}

	public Stream<Routine> getUnbrokens() {
		return toBeBrokens.stream();
	}

	public boolean isAllRoutinesBroken() {
		return toBeBrokens.isEmpty();
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
}
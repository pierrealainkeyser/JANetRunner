package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private List<Routine> all = new ArrayList<>();
	private boolean bypassed;
	private final Ice ice;

	public EncounteredIce(Ice ice) {
		this.ice = ice;
		toBeBrokens.addAll(ice.getRoutines());
		all.addAll(toBeBrokens);
	}

	public void addRoutine(Routine r) {
		toBeBrokens.add(r);
		all.add(r);
	}

	public void addBroken(Routine r) {
		brokens.add(r);
		toBeBrokens.remove(r);
	}

	public boolean isBroken(Routine r) {
		return brokens.contains(r);
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

	public List<Routine> getToBeBrokens() {
		return Collections.unmodifiableList(toBeBrokens);
	}

	public List<Routine> getAll() {
		return all;
	}
}
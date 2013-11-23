package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.IceBreakerType;

public abstract class Ice extends RezzableCard {

	private final int strength;

	private final IceType iceType;

	private final List<Routine> routines = new ArrayList<>();

	public Ice(Influence influence, Cost rezzCost, IceType iceType, int strength) {
		super(influence, rezzCost);
		this.strength = strength;
		this.iceType = iceType;
	}

	/**
	 * Renvoi vrai si le breaker peut casser cette glace
	 * @param breaker
	 * @return
	 */
	public boolean isBrokenBy(IceBreaker breaker) {
		IceBreakerType type = breaker.getIceBreakerType();
		return iceType.isBrokenBy(type) || IceBreakerType.IA == type;
	}

	public List<Routine> getRoutines() {
		return Collections.unmodifiableList(routines);
	}

	protected Ice addRoutine(Routine r) {
		routines.add(r);
		return this;
	}

	public int getStrength() {
		return strength;
	}

	public IceType getIceType() {
		return iceType;
	}

	public int getRoutinesCount() {
		return routines.size();
	}

}

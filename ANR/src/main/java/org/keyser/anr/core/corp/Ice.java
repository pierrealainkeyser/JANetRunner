package org.keyser.anr.core.corp;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.runner.IceBreaker;

public abstract class Ice extends AbstractCardCorp {

	private final int strength;

	private final List<Routine> routines = new ArrayList<>();

	public Ice(Influence influence, Cost rezCost, int strength, CardSubType... subtypes) {
		super(influence, rezCost, subtypes);
		this.strength = strength;
	}

	/**
	 * Renvoi vrai si le breaker peut casser cette glace
	 * 
	 * @param breaker
	 * @return
	 */
	public boolean isBrokenBy(IceBreaker breaker) {

		Collector<CardSubType, ?, List<CardSubType>> list = toList();
		List<CardSubType> breakers = breaker.getSubTypes().stream().filter(CardSubType::isIceBreaker).collect(list);
		List<CardSubType> ices = getSubTypes().stream().filter(CardSubType::isIce).collect(list);
		for (CardSubType b : breakers) {
			for (CardSubType i : ices) {
				if (b.mayBreak(i))
					return true;
			}
		}

		return false;

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
}

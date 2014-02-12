package org.keyser.anr.core.runner;

import java.util.Collection;
import java.util.Collections;

import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Program extends InstallableRunnerCard {

	private final int memoryUnit;

	public Program(Influence influence, Cost cost, int memoryUnit, CardSubType ...subtypes) {
		super(influence, cost,subtypes);
		this.memoryUnit = memoryUnit;
	}

	public int getMemoryUnit() {
		return memoryUnit;
	}
	
	@Override
	public Collection<CardLocation> possibleInstallPlaces() {
		return Collections.singletonList(CardLocation.PROGRAMS);
	}

}

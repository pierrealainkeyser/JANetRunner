package org.keyser.anr.core.runner;

import java.util.List;

import org.keyser.anr.core.AbstractCardFactory;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.MetaCard;

public class ProgramMetaCard extends MetaCard {

	private final int memoryUnit;

	public ProgramMetaCard(String name, Influence influence, Cost cost, boolean unique, String graphic, int memoryUnit, List<CardSubType> subTypes, AbstractCardFactory factory) {
		super(name, influence, cost, unique, graphic, subTypes, factory);
		this.memoryUnit = memoryUnit;
	}

	public int getMemoryUnit() {
		return memoryUnit;
	}
	
	

}

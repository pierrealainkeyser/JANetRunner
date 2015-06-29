package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.AbstractCardFactory;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.MetaCard;

public class IceMetaCard extends MetaCard {

	private final int strength;

	public IceMetaCard(String name, Influence influence, Cost cost,int strength, boolean unique, String graphic, List<CardSubType> subTypes, AbstractCardFactory factory) {
		super(name, influence, cost, unique, graphic, subTypes, factory);
		this.strength = strength;
	}

	public int getStrength() {
		return strength;
	}

	
}

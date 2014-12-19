package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.AbstractCardFactory;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.MetaCard;

public class AgendaMetaCard extends MetaCard {

	public AgendaMetaCard(String name, Influence influence, Cost cost,
			boolean unique, String graphic, List<CardSubType> subTypes,
			AbstractCardFactory factory) {
		super(name, influence, cost, unique, graphic, subTypes, factory);
		// TODO Auto-generated constructor stub
	}

}

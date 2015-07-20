package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.AbstractCardFactory;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.MetaCard;

public class AgendaMetaCard extends MetaCard {

	private final int requirement;

	private final int points;

	public AgendaMetaCard(String name, Influence influence, int requirement, int points, boolean unique, String graphic, List<CardSubType> subTypes, AbstractCardFactory factory) {
		super(name, influence, null, unique, graphic, subTypes, factory);
		this.requirement = requirement;
		this.points = points;
	}

	public int getRequirement() {
		return requirement;
	}

	public int getPoints() {
		return points;
	}

}

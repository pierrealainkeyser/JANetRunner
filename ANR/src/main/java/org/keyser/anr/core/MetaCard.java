package org.keyser.anr.core;

import java.util.Collections;
import java.util.List;

public class MetaCard {
	private final Influence influence;

	private final Cost cost;

	private final boolean unique;

	private final String graphic;

	private final String name;

	private final List<CardSubType> subTypes;

	private final AbstractCardFactory factory;

	public MetaCard(String name, Influence influence, Cost cost, boolean unique, String graphic, List<CardSubType> subTypes, AbstractCardFactory factory) {
		this.name = name;
		this.influence = influence;
		this.cost = cost;
		this.unique = unique;
		this.graphic = graphic;
		this.subTypes = Collections.unmodifiableList(subTypes);
		this.factory = factory;
	}

	/**
	 * Permet de créer une carte
	 * 
	 * @param game
	 * @param id
	 * @return
	 */
	public AbstractCard create(int id) {
		return factory.create(id, this);
	}

	public Cost getCost() {
		return cost;
	}

	public String getGraphic() {
		return graphic;
	}

	public Influence getInfluence() {
		return influence;
	}

	public boolean isUnique() {
		return unique;
	}

	public List<CardSubType> getSubTypes() {
		return subTypes;
	}

	public String getName() {
		return name;
	}
}

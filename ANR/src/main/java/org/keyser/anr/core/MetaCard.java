package org.keyser.anr.core;

import java.util.Collections;
import java.util.List;

public class MetaCard {
	private final Influence influence;

	private final Cost cost;

	private final boolean unique;

	private final String graphic;

	private final List<CardSubType> subTypes;

	public MetaCard(Influence influence, Cost cost, boolean unique,
			String graphic, List<CardSubType> subTypes) {
		super();
		this.influence = influence;
		this.cost = cost;
		this.unique = unique;
		this.graphic = graphic;
		this.subTypes = Collections.unmodifiableList(subTypes);
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
}

package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FIXME gestion du cout
 * 
 * @author PAF
 * 
 */
public abstract class Card extends AbstractGameContent {

	private final Influence influence;

	private final Cost cost;

	private final List<PaidAbility> paidAbilities = new ArrayList<>();

	private int id;

	private CardLocation location;

	public Card(Influence influence, Cost cost) {
		super();
		this.influence = influence;
		this.cost = cost;
	}

	public CardLocation getLocation() {
		return location;
	}

	public void setLocation(CardLocation location) {
		this.location = location;
	}

	protected Card addAction(PaidAbility paidAbility) {
		this.paidAbilities.add(paidAbility);
		return this;
	}

	public List<PaidAbility> getPaidAbilities() {
		return Collections.unmodifiableList(paidAbilities);
	}

	public Faction getFaction() {
		return influence.getFaction();
	}

	public int getInfluence() {
		return influence.getValue();
	}

	public Cost getCost() {
		return cost;
	}

	public int getId() {
		return id;
	}

	public Card setId(int id) {
		this.id = id;
		return this;
	}

}

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

	private final List<AbstractAbility> paidAbilities = new ArrayList<>();

	private int id;

	private CardLocation location;

	public Card(Influence influence, Cost cost) {
		this.influence = influence;
		this.cost = cost;
	}

	public CardLocation getLocation() {
		return location;
	}

	public void setLocation(CardLocation location) {
		Game game = getGame();
		if (this.location != null && game != null) {
			game.removeCardFrom(this, this.location);
		}

		this.location = location;
		notification(NotificationEvent.CARD_LOC_CHANGED.apply().m(this));

		if (this.location != null && game != null) {
			game.addCardFrom(this, this.location);
		}
	}

	protected Card addAction(AbstractAbility paidAbility) {
		this.paidAbilities.add(paidAbility);
		return this;
	}

	public List<AbstractAbility> getPaidAbilities() {
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

	@Override
	public String toString() {
		return "Card [id=" + id + "]";
	}

}

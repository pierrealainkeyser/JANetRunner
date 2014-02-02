package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Arrays;
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

	private Integer credits;

	private Integer powerCounter;

	private boolean unique = false;

	private final List<CardSubType> subTypes = new ArrayList<>();

	public Card(Influence influence, Cost cost, CardSubType... sub) {
		this.influence = influence;
		this.cost = cost;
		this.subTypes.addAll(Arrays.asList(sub));
	}

	/**
	 * Permet de matcher une carte
	 * 
	 * @param ca
	 * @return
	 */
	public boolean equals(CardAccess ca) {
		return ca.getCard() == this;
	}

	public CardLocation getLocation() {
		return location;
	}

	protected boolean hasSubtype(CardSubType s) {
		return subTypes.contains(s);
	}

	/**
	 * Change la position de la carte en se désenregistrant de la zone
	 * précédente.
	 * 
	 * @param location
	 */
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

	protected abstract void doTrash();

	public final void trash(Flow next) {
		doTrash();
		getGame().apply(new CardTrashedEvent(this), next);
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

	public Integer getCredits() {
		return credits;
	}

	public void setCredits(Integer credits) {
		boolean changed = changed(this.credits, credits);
		this.credits = credits;
		if (changed)
			notification(NotificationEvent.CARD_CREDITS.apply().m(this));
	}

	public Integer getPowerCounter() {
		return powerCounter;
	}

	protected final boolean changed(Integer i, Integer o) {
		int vi = i != null ? i : -1;
		int vo = o != null ? o : -1;

		return vi != vo;

	}

	public void setPowerCounter(Integer powerCounter) {
		boolean changed = changed(this.powerCounter, powerCounter);
		this.powerCounter = powerCounter;
		if (changed)
			notification(NotificationEvent.CARD_POWER_COUNTER.apply().m(this));
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public void addSubtype(CardSubType cst) {
		subTypes.add(cst);
	}

	public void removeSubtype(CardSubType cst) {
		subTypes.remove(cst);
	}

	public List<CardSubType> getSubTypes() {
		return Collections.unmodifiableList(subTypes);
	}
}

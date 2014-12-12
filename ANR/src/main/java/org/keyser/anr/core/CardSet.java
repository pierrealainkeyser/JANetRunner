package org.keyser.anr.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Un ensemble de cards
 * 
 * @author PAF
 * 
 */
public class CardSet {
	private Set<Integer> cards = new HashSet<>();

	public Set<Integer> getCards() {
		return cards;
	}

	public void setCards(Set<Integer> cards) {
		this.cards = cards;
	}

	public void add(AbstractCard c) {
		cards.add(c.getId());
	}

	public boolean contains(AbstractCard c) {
		return cards.contains(c.getId());
	}
}

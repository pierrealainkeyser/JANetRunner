package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Un ensemble de cards
 * 
 * @author PAF
 * 
 */
public class AbstractCardList implements Iterable<AbstractCard> {
	private List<AbstractCard> cards = new ArrayList<>();

	public AbstractCardList(List<AbstractCard> cards) {
		this.cards = cards;
	}

	public AbstractCardList() {
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	public List<AbstractCard> getCards() {
		return cards;
	}

	public void setCards(List<AbstractCard> cards) {
		this.cards = cards;
	}

	public void add(AbstractCard c) {
		cards.add(c);
	}

	@Override
	public Iterator<AbstractCard> iterator() {
		return cards.iterator();
	}

	public int size() {
		return cards.size();
	}

	public Stream<AbstractCard> stream() {
		return cards.stream();
	}
}

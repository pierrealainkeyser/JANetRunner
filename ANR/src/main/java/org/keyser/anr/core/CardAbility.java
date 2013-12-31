package org.keyser.anr.core;

/**
 * Une ability d'une carte
 * 
 * @author PAF
 * 
 */
public class CardAbility extends AbstractAbility {

	private final Card card;

	protected CardAbility(Card card, String name, Cost cost) {
		this(card, name, cost, null);

	}

	protected CardAbility(Card card, String name, Cost cost, Object action) {
		super(name, cost, action);
		this.card = card;
	}

	@Override
	protected void registerQuestion(Question q) {
		q.ask(getName(), card).to(this::doNext).setCost(getCost());
	}

}

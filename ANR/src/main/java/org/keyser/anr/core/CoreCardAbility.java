package org.keyser.anr.core;

public class CoreCardAbility extends CoreAbility {

	private final Card card;

	public CoreCardAbility(Card card, String name, Cost cost) {
		super(name, cost);
		this.card = card;
	}

	@Override
	protected void registerQuestion(Question q) {
		q.ask(getName(), card).to(this::doNext).setCost(getCost());
	}

	public Card getCard() {
		return card;
	}
}

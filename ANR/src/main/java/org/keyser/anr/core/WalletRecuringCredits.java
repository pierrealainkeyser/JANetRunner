package org.keyser.anr.core;

import static org.keyser.anr.core.EventMatcher.match;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletRecuringCredits extends WalletUnit {

	static final Logger log = LoggerFactory.getLogger(WalletRecuringCredits.class);

	private final String faction;

	private final Card card;

	private final int amount;
	
	public WalletRecuringCredits(String faction, Card card, Predicate<?> target, int amount, Class<? extends Event> t) {
		super(0, CostCredit.class, CostCredit::new, target);
		this.faction = faction;
		this.card = card;
		this.amount = amount;

		String str = card != null ? card.getId() + "" : faction;

		add(match(t).name("Init recuring credits :" + str).first().auto().call(this::reload));
	}

	private void reload() {
		setAmount(amount);
	}

	public String getFaction() {
		return faction;
	}

	public Card getCard() {
		return card;
	}
}

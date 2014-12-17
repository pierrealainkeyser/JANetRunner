package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardRunner extends AbstractCard {

	public static final Predicate<AbstractCard> IS_ICEBREAKER = hasSubtypes(CardSubType.ICE_BREAKER);

	protected AbstractCardRunner(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.RUNNER, CardLocation::isInRunnerHand);
	}
	
	@Override
	public PlayerType getOwner() {
		return PlayerType.RUNNER;
	}
}

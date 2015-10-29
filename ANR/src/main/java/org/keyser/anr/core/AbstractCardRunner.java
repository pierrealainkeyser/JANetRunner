package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardRunner extends AbstractCard {

	public static final Predicate<AbstractCard> IS_ICEBREAKER = hasAnyTypes(CardSubType.ICE_BREAKER);

	protected AbstractCardRunner(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.RUNNER, CardLocation::isInRunnerHand);
	}
	
	public void defaultInstallChat() {
		game.chat("{0} installs {1}", getRunner(), this);
	}
	
	/**
	 * Déplacemement de la carte au heap
	 * 
	 * @param next
	 */
	@Override
	protected void setTrashCause(TrashCause ctx) {
		setRezzed(true);
		super.setTrashCause(ctx);
		getRunner().getHeap().add(this);		
	}

	
	@Override
	public PlayerType getOwner() {
		return PlayerType.RUNNER;
	}
}

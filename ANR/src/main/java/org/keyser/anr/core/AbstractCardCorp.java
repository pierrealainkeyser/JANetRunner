package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardCorp extends AbstractCard {

	public static final Predicate<AbstractCard> IS_CORP_CARD = ac -> ac instanceof AbstractCardCorp;

	public static final Predicate<AbstractCard> IS_ADVANCEABLE = IS_CORP_CARD.and(ac -> ((AbstractCardCorp) ac).isAdvanceable() && ac.isInstalled());

	protected AbstractCardCorp(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.CORP, CardLocation::isInCorpHand);
	}

	public boolean isAdvanceable() {
		return false;
	}

	/**
	 * Il faut un evenement pour déplacer la carte
	 * 
	 * @param next
	 */
	@Override
	public void trash(TrashCause ctx, Flow next) {
		super.trash(ctx, () -> {
			getCorp().getArchives().add(this);
			next.apply();
		});
	}

	@Override
	public PlayerType getOwner() {
		return PlayerType.CORP;
	}
}

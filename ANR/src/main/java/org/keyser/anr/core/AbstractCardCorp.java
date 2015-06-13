package org.keyser.anr.core;

import java.util.function.Predicate;

import org.keyser.anr.core.corp.RezzAbstractCardAction;

public class AbstractCardCorp extends AbstractCard {

	public static final Predicate<AbstractCard> IS_CORP_CARD = ac -> ac instanceof AbstractCardCorp;

	public static final Predicate<AbstractCard> IS_ADVANCEABLE = IS_CORP_CARD.and(ac -> ((AbstractCardCorp) ac).isAdvanceable() && ac.isInstalled());

	protected AbstractCardCorp(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.CORP, CardLocation::isInCorpHand);

		match(CollectHabilities.class, em -> em.test(ch -> isInstalled() && !isRezzed()).call(this::registerRezz));
	}

	private void registerRezz(CollectHabilities hab) {
		UserAction rezz = new UserAction(getCorp(), this, new CostForAction(getCost(), new RezzAbstractCardAction<>(this)), "Rezz");		
		hab.add(rezz.spendAndApply(this::doRezz));
	}

	private void doRezz(UserAction ua, Flow next) {
		setRezzed(true);
		game.chat("{0} rezz {1} for {2}", getCorp(), this, ua.getCost().getCost());
		next.apply();
	}

	public boolean isRezzable() {
		return false;
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

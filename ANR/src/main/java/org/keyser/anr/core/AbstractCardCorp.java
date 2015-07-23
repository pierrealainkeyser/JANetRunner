package org.keyser.anr.core;

import java.util.Optional;
import java.util.function.Predicate;

import org.keyser.anr.core.corp.AdvanceAbstractCardAction;
import org.keyser.anr.core.corp.RezzAbstractCardAction;

public class AbstractCardCorp extends AbstractCard {

	public static final Predicate<AbstractCard> IS_CORP_CARD = ac -> ac instanceof AbstractCardCorp;

	public static final Predicate<AbstractCard> IS_ADVANCEABLE = IS_CORP_CARD.and(ac -> ((AbstractCardCorp) ac).isAdvanceable() && ac.isInstalled());

	protected AbstractCardCorp(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.CORP, CardLocation::isInCorpHand);

		// permet de rezzed
		match(CollectHabilities.class, em -> em.test(ch -> isInstalled() && ch.getType() == getOwner() && isRezzable() && !isRezzed()).call(this::registerRezz));

		match(CollectHabilities.class, em -> em.test(ch -> isInstalled() && ch.getType() == getOwner() && isAdvanceable()).call(this::registerAdvance));
	}

	/**
	 * Création d'un effet temporaire d'un type donné
	 * @param type
	 * @return
	 */
	public <T extends CoolEffect> Optional<T> createCoolEffect(Class<T> type){
		return Optional.empty();
	}

	private void registerAdvance(CollectHabilities hab) {
		UserAction rezz = new UserAction(getCorp(), this, new CostForAction(Cost.credit(1).withAction(1), new AdvanceAbstractCardAction<>(this)), "Advance");
		hab.add(rezz.spendAndApply(this::doAdvance));
	}

	private void registerRezz(CollectHabilities hab) {
		UserAction rezz = new UserAction(getCorp(), this, new CostForAction(getCost(), new RezzAbstractCardAction<>(this)), "Rezz");
		hab.add(rezz.spendAndApply(this::doRezz));
	}

	protected void onRezzed(Flow next) {
		next.apply();
	}

	/**
	 * Permet de rézzer une carte
	 * @param ua
	 * @param next
	 */
	public void doRezz(UserAction ua, Flow next) {
		setRezzed(true);
		Cost cost = ua.getCost().getCost();
		game.chat("{0} rezz {1} for {2}", getCorp(), this, cost);
		onRezzed(next);
	}

	private void doAdvance(UserAction ua, Flow next) {

		game.chat("{0} advance {1} for {2}", getCorp(), this, ua.getCost().getCost());
		addToken(TokenType.ADVANCE, 1);
		next.apply();

	}

	public boolean isRezzable() {
		return false;
	}

	public boolean isAdvanceable() {
		return false;
	}

	/**
	 * Il faut un evenement pour dÃ©placer la carte
	 * 
	 * @param next
	 */
	@Override
	protected void setTrashCause(TrashCause ctx) {
		super.setTrashCause(ctx);
		getCorp().getArchives().add(this);
	}

	@Override
	public PlayerType getOwner() {
		return PlayerType.CORP;
	}
}

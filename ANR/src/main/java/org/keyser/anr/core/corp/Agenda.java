package org.keyser.anr.core.corp;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.PlayCardAction;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.UserAction;

public abstract class Agenda extends AssetOrAgenda {

	protected Agenda(int id, MetaCard meta) {
		super(id, meta);

		Predicate<CollectHabilities> scorablePredicate = ch -> isInstalled() && ch.getType() == getOwner() && isScorable();
		match(CollectHabilities.class, em -> em.test(scorablePredicate).call(this::registerScore));
	}

	private void registerScore(CollectHabilities collect) {
		UserAction scoreAgenda = new UserAction(getCorp(), this, createScoringCost(), "Score");
		collect.add(scoreAgenda.spendAndApply(this::doScore));
	}

	private CostForAction createScoringCost() {
		return new CostForAction(Cost.free(), new ScoreAgendaAction(this));
	}

	/**
	 * Permet de scorer l'agenda
	 * 
	 * @param next
	 */
	private void doScore(Flow next) {

		// gestion de la position
		Corp corp = getCorp();
		corp.addToScore(this);
		
		onScored(next.wrap(this::cleanUpScore));
	}

	private void cleanUpScore(Flow next) {
		// supprimer les tokens d'avancement
		this.setToken(TokenType.ADVANCEMENT, 0);
	}

	protected void onScored(Flow next) {
		next.apply();
	}

	@Override
	protected PlayCardAction<? extends AbstractCardCorp> playAction() {
		return new PlayAgendaAction(this);
	}

	public boolean isScored() {
		return getLocation().isScoredByCorp();
	}

	public boolean isScorable() {
		// TODO logique de score
		return false;
	}

	@Override
	public boolean isAdvanceable() {
		return !isScored();
	}
}

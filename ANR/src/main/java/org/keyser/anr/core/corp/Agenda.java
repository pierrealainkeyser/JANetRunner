package org.keyser.anr.core.corp;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
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

	public int getRequirement() {
		return getMeta().getRequirement();
	}
	

	public int getPoints() {
		return getMeta().getPoints();
	}

	@Override
	protected AgendaMetaCard getMeta() {
		return (AgendaMetaCard) super.getMeta();
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
		this.setToken(TokenType.ADVANCE, 0);
		next.apply();
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

	/**
	 * Permet de savoir si l'agenda est scorable. La logique est placée dans {@link DetermineAgendaRequirement}
	 * @return
	 */
	public boolean isScorable() {

		if (isScored())
			return false;

		Game game = getGame();
		if (!game.getTurn().mayScoreAgenda())
			return false;

		// il faut envoyer un evenement dans le moteur !!
		DetermineAgendaRequirement dar = new DetermineAgendaRequirement(this);
		game.fire(dar);
		return dar.isScorable();

	}

	@Override
	public boolean isAdvanceable() {
		return !isScored();
	}
}

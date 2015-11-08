package org.keyser.anr.core.corp;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectAbstractHabilites;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.PlayCardAction;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.UserAction;

public abstract class Agenda extends AssetOrAgenda {

	protected Agenda(int id, MetaCard meta) {
		super(id, meta);

		Predicate<CollectHabilities> scorablePredicate = ch -> isInstalled() && ch.getType() == getOwner() && isScorable();
		match(CollectHabilities.class, em -> em.test(scorablePredicate).call(this::registerScore));
	}

	@Override
	public boolean hasAccesInArchives() {
		return true;
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

	private void registerScore(CollectAbstractHabilites collect) {
		UserAction scoreAgenda = new UserAction(getCorp(), this, createScoringCost(), "Score");
		collect.add(scoreAgenda.spendAndApply(this::prepareScore));
	}

	private CostForAction createScoringCost() {
		return new CostForAction(Cost.free(), new ScoreAgendaAction(this));
	}

	/**
	 * D�place l'agenda en zone de score et envoi l'evenement de score de
	 * l'agenda
	 * 
	 * @param next
	 */
	private void prepareScore(Flow next) {
		// gestion de la position
		Corp corp = getCorp();
		corp.addToScore(this);

		game.apply(new AgendaScoredEvent(this), next.wrap(this::localScoreEffect));
	}

	/**
	 * L'agenda est volé
	 * 
	 * @param next
	 */
	public void doSteal(Flow next) {
		Runner runner = getRunner();
		runner.addToScore(this);

		// on envoi la notification et on score
		game.apply(new AgendaStolenEvent(this), next.wrap(this::cleanUpScore));
	}

	/**
	 * Permet d'appliquer les traitements locaux pour le score de l'agenda
	 * 
	 * @param next
	 */
	private void localScoreEffect(Flow next) {

		// on envoi l'evenememnt pour pouvoir r�agir
		onScored(next.wrap(this::cleanUpScore));

	}

	/**
	 * Fait du traitement de scoring. On nettoye les tokens
	 * 
	 * @param next
	 */
	private void cleanUpScore(Flow next) {
		// supprimer les tokens d'avancement
		this.setToken(TokenType.ADVANCE, 0);
		this.setInstalled(false);
		next.apply();
	}

	/**
	 * M�thode � surcharger pour ajouter un comportement particulier
	 * 
	 * @param next
	 */
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
	 * Permet de savoir si l'agenda est scorable. La logique est plac�e dans
	 * {@link DetermineAgendaRequirement}
	 * 
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

package org.keyser.anr.core;

import static org.keyser.anr.core.SimpleFeedback.noop;

import java.util.Collection;
import java.util.function.Predicate;

import org.keyser.anr.core.corp.Ice;

public class Turn {
	private abstract class AbstractPingPong {

		protected PlayerType active;

		protected AbstractPingPong(PlayerType active) {
			this.active = active;
		}

		protected void collectFeedbacks(boolean action, EventConsumer<Feedback<?, ?>> feedbackConsumer, Flow next) {
			CollectHabilities collect = new CollectHabilities(active, action);
			game.fire(collect);

			Collection<Feedback<?, ?>> feedbacks = collect.getFeedbacks();
			boolean hasFeedbacks = false;
			for (Feedback<?, ?> feedback : feedbacks) {
				if (feedback.checkCost()) {
					game.user(feedback, next.wrap(feedbackConsumer.wrap(feedback)));
					hasFeedbacks = true;
				}
			}

			// quand il y a une action, il ne faut jamais posser la question de
			// la continuation
			if (!action) {
				if (hasFeedbacks || requireQuestion()) {
					// TODO gestion de la carte primaire pour le done. Par
					// défautsur l'ID, mais c'est pas bon pour l'approche ou la
					// rencontre d'une glace
					AbstractId me = game.getId(active);
					game.user(noop(me, me, "Done"), next);
				} else
					next.apply();
			}
		}

		protected void collectFeedbacks(EventConsumer<Feedback<?, ?>> e, Flow next) {
			collectFeedbacks(false, e, next);
		}

		private boolean requireQuestion() {

			if (active == PlayerType.CORP) {
				boolean unrezzedCorpCard = game.getCards().stream().anyMatch(UNREZZED_INSTALL_CORP_CARDS);
				return unrezzedCorpCard || mayRezzIce();
			}
			return false;
		}

		protected void swapPlayer() {
			active = active.next();
		}

		protected EventConsumer<Feedback<?, ?>> to(FlowArg<Flow> to) {
			return (feed, next) -> to.apply(next);
		}

	}

	private class ActionPingPong extends AbstractPingPong {

		protected ActionPingPong(PlayerType active) {
			super(active);
			// TODO Auto-generated constructor stub
		}

		/**
		 * Tant que le joueur ne joue pas d'action il a la priorité
		 * 
		 * @param fired
		 * @param next
		 */
		private void fired(Feedback<?, ?> fired, Flow next) {
			if (fired.wasAnAction()) {
				// on swappe de joueur
				swapPlayer();
				secondPlayer(next);
			} else {
				// on recommence l'action
				firstPlayer(next);
			}
		}

		public void firstPlayer(Flow next) {
			collectFeedbacks(true, this::fired, next);
		}

		private void secondPlayer(Flow next) {
			collectFeedbacks(to(this::secondPlayer), next);

		}		
	}

	private class EventPingPong extends AbstractPingPong {

		protected EventPingPong(PlayerType active) {
			super(active);
		}

		/**
		 * Demande au second joueur, et sort si pas d'action ou passe au second
		 * 
		 * @param next
		 */
		private void firstDone(Flow next) {
			swapPlayer();
			collectFeedbacks(to(this::secondPlayer), next);
		}

		/**
		 * boucle sur le premier joueur ou passe à first done si pas d'action
		 * 
		 * @param next
		 */
		public void firstPlayer(Flow next) {
			collectFeedbacks(to(this::firstPlayer), next.wrap(this::firstDone));
		}

		/**
		 * Demande au premier joueur, et si pas d'action ou passe au premier
		 * 
		 * @param next
		 */
		private void secondDone(Flow next) {
			swapPlayer();
			collectFeedbacks(to(this::firstPlayer), next);
		}

		/**
		 * boucle sur le second joueur ou passeà secondDone si pas d'action
		 * 
		 * @param next
		 */
		private void secondPlayer(Flow next) {
			collectFeedbacks(to(this::secondPlayer), next.wrap(this::secondDone));
		}

	}

	public enum Phase {
		ACTION, DISCARD, DRAW, STARTING
	}

	private final static Predicate<? super AbstractCard> UNREZZED_INSTALL_CORP_CARDS = ac -> ac instanceof AbstractCardCorp && !(ac instanceof Ice) && ac.isInstalled() && !ac.isRezzed();

	private final PlayerType active;

	private final Game game;

	private Phase phase;

	private final int turn;

	public Turn(PlayerType active, Game game, int turn) {
		this.active = active;
		this.game = game;
		this.turn = turn;
	}

	public void actionPhase() {

		//
		AbstractId id = game.getId(active);
		if (id.hasAction()) {

			setPhase(Phase.ACTION);

			// on comme par l'utilisateur
			new ActionPingPong(active).firstPlayer(this::actionPhase);
		} else {
			discardPhase();

		}
	}

	public void discardPhase() {
		setPhase(Phase.DISCARD);

		// on comme par l'utilisateur
		new EventPingPong(active).firstPlayer(this::terminate);
	}

	public void drawPhase() {
		setPhase(Phase.DRAW);
		game.getCorp().draw(1, this::startTurn);
	}

	public Phase getPhase() {
		return phase;
	}

	public boolean mayRezzIce() {
		// TODO
		return false;
	}

	private void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Turn start() {

		if (active == PlayerType.CORP) {
			drawPhase();
		} else {
			startTurn();
		}

		return this;
	}

	private void startingPhase() {
		new EventPingPong(active).firstPlayer(this::actionPhase);
	}

	private void startTurn() {

		setPhase(Phase.STARTING);

		// TODO mise en place des actions

		game.apply(new StartOfTurn(), this::startingPhase);
	}

	private void terminate() {

	}

}

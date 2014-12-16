package org.keyser.anr.core;

import static org.keyser.anr.core.SimpleFeedback.noop;

import java.util.Collection;
import java.util.function.Predicate;

public class Turn {
	private final PlayerType active;

	private final Game game;

	private final int turn;

	private Phase phase;

	private final static Predicate<? super AbstractCard> UNREZZED_INSTALL_CORP_CARDS = ac -> ac instanceof AbstractCardCorp
			&& !ac instanceof Ice && ac.isInstalled() && !ac.isRezzed();

	public enum Phase {
		STARTING, DRAW, ACTION, DISCARD
	}

	public Turn(PlayerType active, Game game, int turn) {
		this.active = active;
		this.game = game;
		this.turn = turn;
	}

	public Turn start() {

		if (active == PlayerType.CORP) {
			drawPhase();
		} else {
			startTurn();
		}

		return this;
	}

	private void startTurn() {

		setPhase(Phase.STARTING);
		game.apply(new StartOfTurn(), this::readyAction);
	}

	private void readyAction() {

		// TODO initiation des actions
		actionPhase();
	}

	public void drawPhase() {
		setPhase(Phase.DRAW);
		game.getCorp().draw(1, this::startTurn);
	}

	public void actionPhase() {

		//
		AbstractId id = game.getId(active);
		if (id.hasAction()) {

			setPhase(Phase.ACTION);

			// on comme par l'utilisateur
			new PingPong(active).ask(this::actionPhase);
		} else {
			discardPhase();

		}
	}

	private static interface Function<T, X> {
		X apply(T t);
	}

	private class ActionPingPong {

		private PlayerType active;

		public void firstPlayer(Flow next) {
			loop(this::fired, next);
		}

		private boolean loop(EventConsumer<Feedback<?, ?>> e, Flow next) {
			CollectHabilities collect = new CollectHabilities(active);
			game.fire(collect);

			Collection<Feedback<?, ?>> feedbacks = collect.getFeedbacks();
			boolean feeds = false;
			for (Feedback<?, ?> feedback : feedbacks) {
				if (feedback.checkCost()) {
					game.user(feedback, next.wrap(e.wrap(feedback)));
					feeds = true;
				}
			}
			return feeds;
		}

		/**
		 * Tant que le joueur ne joue pas d'action il a la priorit�
		 * @param fired
		 * @param next
		 */
		private void fired(Feedback<?, ?> fired, Flow next) {
			if (fired.wasAnAction()) {
				// on swappe de joueur
				active = active.next();
				secondPlayer(next);
			} else {
				// on recommence l'action
				firstPlayer(next);
			}
		}

		private void secondPlayer(Flow next) {
			boolean hasFeeds = loop(
					(f, continuation) -> secondPlayer(continuation), next);

			// si il y a des feeds ou si corp avec des trucs qui ne sont pas des
			// glaces install�s, pas rezz� (il y a l'opportunite de rezze)
			if (hasFeeds) {
				AbstractId me = game.getId(active);
				game.user(noop(me, me, "Done"), next);
			} else
				next.apply();
		}

	}

	private class PingPong {

		private PlayerType active;

		public PingPong(PlayerType active) {
			this.active = active;
		}

		private boolean isActivePlayer() {
			return Turn.this.active == active;
		}

		private boolean isCorp() {
			return active == PlayerType.CORP;
		}

		public void ask(Flow next) {

			CollectHabilities collect = new CollectHabilities(active);
			game.fire(collect);

			// TODO gestion du contexte

			boolean hasFeedback = false;
			Collection<Feedback<?, ?>> feedbacks = collect.getFeedbacks();
			for (Feedback<?, ?> feedback : feedbacks) {
				if (feedback.checkCost()) {
					game.user(feedback, () -> fired(feedback, next));
					hasFeedback = true;
				}
			}

			boolean requireNoop = false;
			if (!isActivePlayer()) {
				// si il y a des options on propose de les zaper
				if (hasFeedback)
					requireNoop = true;
				else if (isCorp()) {
					// pas d'option mais des cartes cach�s, donc des options
					requireNoop = game.getCards().stream()
							.anyMatch(UNREZZED_INSTALL_CORP_CARDS);
				}
			}

			if (requireNoop) {
				AbstractId me = game.getId(active);
				game.user(noop(me, me, "Nothing"), next.wrap(this::doNoOp));
			} else {
				if (!hasFeedback)
					end(next);
			}
		}

		private void doNoOp(Flow next) {
			if (isActivePlayer())
				nextPlayer(next);
			else
				end(next);
		}

		private void nextPlayer(Flow next) {
			// passage au joueur suivant
			active = active.next();
			ask(next);
		}

		private void end(Flow next) {
			next.apply();
		}

		private void fired(Feedback<?, ?> fired, Flow next) {
			if (fired.wasAnAction()) {
				// on swappe et on recommence
				nextPlayer(next);
			} else {
				// on recommence
				ask(next);
			}
		}
	}

	public void discardPhase() {
		setPhase(Phase.DISCARD);

		// on comme par l'utilisateur
		new PingPong(active).ask(this::terminate);
	}

	private void terminate() {

	}

	public Phase getPhase() {
		return phase;
	}

	private void setPhase(Phase phase) {
		this.phase = phase;
	}

}

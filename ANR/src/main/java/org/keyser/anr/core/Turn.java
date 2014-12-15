package org.keyser.anr.core;

import static org.keyser.anr.core.SimpleFeedback.noop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Turn {
	private final PlayerType active;

	private final Game game;

	private final int turn;

	private Phase phase;

	public enum Phase {
		UNSTARTED, DRAW, ACTION, DISCARD

	}

	public Turn(PlayerType active, Game game, int turn) {
		this.active = active;
		this.game = game;
		this.turn = turn;
	}

	public Turn start() {
		setPhase(Phase.UNSTARTED);

		// TODO remise en place des actions pour le jour
		Flow to = active == PlayerType.CORP ? this::drawPhase
				: this::actionPhase;

		game.apply(new StartOfTurn(), to);

		return this;
	}

	private void drawPhase() {
		setPhase(Phase.DRAW);
		game.getCorp().draw(1, this::actionPhase);
	}

	private void actionPhase() {
		setPhase(Phase.ACTION);
	}

	private class PingPong {

		private PlayerType active;

		private boolean requireNoop;

		private boolean allowNoop;
		
		

		public void ask(Flow next) {

			CollectHabilities collect = new CollectHabilities(active);
			game.fire(collect);

			//TODO gestion du titre
			
			Collection<Feedback<?, ?>> feedbacks = collect.getFeedbacks();
			List<Feedback<?, ?>> possibles = new ArrayList<>();
			for (Feedback<?, ?> feedback : feedbacks) {
				if (feedback.checkCost()) {
					game.user(feedback, () -> fired(feedback, next));
					possibles.add(feedback);
				}
			}
			
			if (requireNoop) {
				AbstractId me = game.getId(active);
				game.user(noop(me, me, null, "Nothing"),
						next.wrap(this::doNoOp));
			} else {
				if (possibles.isEmpty())
					next.apply();
			}
		}

		private void doNoOp(Flow next) {

		}

		private void fired(Feedback<?, ?> fired, Flow next) {
			if (fired.wasAnAction()) {

				// on swappe et on recommence
			} else {
				// on peut continuer
				ask(next);
			}

		}
	}

	private void discardPhase() {
		setPhase(Phase.DISCARD);
	}

	public Phase getPhase() {
		return phase;
	}

	private void setPhase(Phase phase) {
		this.phase = phase;
	}

}

package org.keyser.anr.core;

import static org.keyser.anr.core.SimpleFeedback.noop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.runner.DoDamageEvent;
import org.keyser.anr.core.runner.DoDamageEvent.DamageType;
import org.keyser.anr.core.runner.IceBreaker;

public class Turn {
	private abstract class AbstractPingPong {

		protected PlayerType active;

		protected AbstractPingPong(PlayerType active) {
			this.active = active;
		}

		protected void collectFeedbacks(boolean action, EventConsumer<Feedback<?, ?>> feedbackConsumer, Flow next) {

			// création d'un serveur vide au besoin
			game.getCorp().ensureEmptyServer();

			CollectAbstractHabilites collect = new CollectHabilities(active, action);
			game.fire(collect);

			Collection<Feedback<?, ?>> feedbacks = collect.getFeedbacks();
			boolean hasFeedbacks = false;

			String text = "Play events";
			if (action)
				text = "Play an action";

			AbstractId id = game.getId(active);
			AbstractCard source = id;
			UserActionContext.Type type = UserActionContext.Type.BASIC;

			Optional<Run> optRun = getRun();
			if (optRun.isPresent()) {
				Run r = optRun.get();
				if (r.isIceBased()) {

					// on se centre sur la glace
					source = r.getIce().get().getIce();
					type = UserActionContext.Type.POP_CARD;
				}
			}

			game.userContext(source, text, type);

			List<Feedback<?, ?>> checkeds = new ArrayList<>();
			for (Feedback<?, ?> feedback : feedbacks) {
				if (feedback.checkCost()) {
					checkeds.add(feedback);
					hasFeedbacks = true;
				}
			}

			// quand il y a une action, il ne faut jamais posser la question de
			// la continuation
			if (!action) {
				if (hasFeedbacks || requireQuestion()) {
					// TODO gestion de la carte primaire pour le done. Par
					// défaut sur l'ID, mais c'est pas bon pour l'approche ou
					// la
					// rencontre d'une glace
					game.user(noop(id, source, "Done"), next);
				} else
					next.apply();
			}
			
			for (Feedback<?, ?> feedback : checkeds) 
				game.user(feedback, next.wrap(feedbackConsumer.wrap(feedback)));
		}

		protected void collectFeedbacks(EventConsumer<Feedback<?, ?>> e, Flow next) {
			collectFeedbacks(false, e, next);
		}

		/**
		 * Si c'est à la corporation de jouer et qu'il y a des cartes face
		 * cachées on peut les rezzers, en tout cas le runner ne doit pas savoir
		 * que la corpo n'a pas le budget
		 * 
		 * @return
		 */
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
		 * boucle sur le premier joueur ou passe � first done si pas d'action
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
		 * boucle sur le second joueur ou passe� secondDone si pas d'action
		 * 
		 * @param next
		 */
		private void secondPlayer(Flow next) {
			collectFeedbacks(to(this::secondPlayer), next.wrap(this::secondDone));
		}

	}

	private final static Predicate<? super AbstractCard> UNREZZED_INSTALL_CORP_CARDS = ac -> ac instanceof AbstractCardCorp && !(ac instanceof Ice) && ac.isInstalled() && !ac.isRezzed();

	private final PlayerType active;

	private final Game game;

	private TurnPhase phase;

	private final int turn;

	private Flow next;

	private final List<DoDamageEvent> damagesEvents = new ArrayList<>();

	private final List<Run> runs = new ArrayList<>();

	public Turn(PlayerType active, Game game, int turn) {
		this.active = active;
		this.game = game;
		this.turn = turn;
	}

	public void newRun(int id, CorpServer server, Flow next) {
		Run r = new Run(game, id, next, server);
		this.runs.add(r);

		game.apply(new RunStatusEvent(r), rse -> r.begin());

	}

	/**
	 * Accéde au run en cours (le dernier en pas cleared)
	 * 
	 * @return
	 */
	public Optional<Run> getRun() {
		if (!runs.isEmpty()) {
			Run last = runs.get(runs.size() - 1);
			if (!last.isCleared())
				return Optional.of(last);
		}
		return Optional.empty();
	}

	/**
	 * Permet de savoir s'il y a eu
	 * 
	 * @param status
	 * @return
	 */
	public boolean anyRun(Run.Status status) {
		return runs.stream().filter(r -> r.getStatus() == status).findAny().isPresent();
	}

	public boolean corpTurn() {
		return active == PlayerType.CORP;
	}

	public boolean runnerTurn() {
		return active == PlayerType.RUNNER;
	}

	public int getTurn() {
		return turn;
	}

	/**
	 * Renvoi vrai s'il y a dejà eu un dommage de se type
	 * 
	 * @param type
	 * @return
	 */
	public boolean hasSuffered(DamageType type) {
		return damagesEvents.stream().anyMatch(d -> d.getType() == type);
	}

	public void addDamageEvent(DoDamageEvent evt) {
		damagesEvents.add(evt);
	}

	public void actionPhase() {

		//
		AbstractId id = game.getId(active);
		if (id.hasAction()) {

			setPhase(TurnPhase.ACTION);

			// on comme par l'utilisateur
			new ActionPingPong(active).firstPlayer(this::actionPhase);
		} else {
			// permet d'avoir des évenements en fin de phase de d'action
			setPhase(TurnPhase.ACTION_WILL_END);

			new EventPingPong(active).firstPlayer(this::discardPhase);
		}
	}

	public void discardPhase() {
		setPhase(TurnPhase.DISCARD);

		AbstractId id = game.getId(active);
		DetermineMaxHandSizeEvent max = new DetermineMaxHandSizeEvent(id);
		game.fire(max);

		// le joueur doit d�fausser ce qu'il peut
		id.discardUntil(max.computeMaxHandSize(), this::terminate);

	}

	public void drawPhase() {
		setPhase(TurnPhase.DRAW);
		game.getCorp().draw(1, this::willStartTurn);
	}

	public TurnPhase getPhase() {
		return phase;
	}

	public boolean mayRezzIce() {
		return testRun(Run::mayRezzIce);
	}

	public boolean testRun(Predicate<Run> pred) {
		Optional<Run> run = getRun();
		if (run.isPresent())
			return pred.test(run.get());

		return false;
	}

	public boolean mayUseIceBreaker() {
		return testRun(Run::mayUseBreaker);
	}

	public boolean mayUseBreakerToBreak(IceBreaker breaker) {
		return testRun(r -> r.mayUseBreakerToBreak(breaker));
	}

	public boolean mayPlayAction() {
		return TurnPhase.ACTION == phase;
	}

	public boolean mayScoreAgenda() {
		return (TurnPhase.ACTION == phase || TurnPhase.ACTION_WILL_START == phase || TurnPhase.ACTION_WILL_END == phase) && PlayerType.CORP == active;
	}

	private void setPhase(TurnPhase phase) {
		this.phase = phase;
	}

	public Turn start(Flow next) {
		this.next = next;
		game.chat("It's now {0}'s turn", active);
		initPhase();
		return this;
	}

	/**
	 * Réalise des échanges uniquement des evenements, pas d'action
	 * 
	 * @param next
	 */
	public void pingpong(Flow next) {
		new EventPingPong(active).firstPlayer(next);
	}

	private void initPhase() {
		setPhase(TurnPhase.INITING);

		// démarrage technique, mise en place des actions
		game.fire(new InitTurn());
		AbstractId id = game.getId(active);

		if (active == PlayerType.CORP) {
			id.setActiveAction(3);
			drawPhase();
		} else {
			id.setActiveAction(4);
			willStartTurn();
		}
	}

	private void willStartTurn() {
		setPhase(TurnPhase.ACTION_WILL_START);

		pingpong(this::startTurn);
	}

	private void startTurn() {
		setPhase(TurnPhase.ACTION);
		game.apply(new StartOfTurn(), this::actionPhase);
	}

	private void terminate() {
		game.apply(new EndOfTurn(), next);
	}

	public PlayerType getActive() {
		return active;
	}

}

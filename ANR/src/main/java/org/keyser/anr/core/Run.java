package org.keyser.anr.core;

import java.util.Iterator;
import java.util.Optional;

import org.keyser.anr.core.UserActionContext.Type;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.ReadyedRoutine;
import org.keyser.anr.core.runner.IceBreaker;

public class Run {

	public static enum Status {
		SUCCESFUL, UNSUCCESFUL, UNKNOW, IN_PROGRESS, ENDED;
	}

	public static enum Step {
		APPROCHING_ICE, ENCOUTERING_ICE, PASSING_ICE, APPROCHING_SERVER, ACCESSING
	}

	private boolean mayJackOff = true;

	private Status status = Status.IN_PROGRESS;

	private CorpServer server;

	private final Flow next;

	private final int id;

	private boolean cleared = false;

	private final Game game;

	private Step step;

	private int depth;

	private EncounteredIce ice;

	private Iterator<ReadyedRoutine> routinesToProcess;

	private boolean firstIce = true;

	public Run(Game game, int id, Flow next, CorpServer server) {
		this.game = game;
		this.id = id;
		this.next = next;
		setServer(server);
	}

	public boolean isIceBased() {
		return mayRezzIce() || mayUseBreaker();
	}

	public boolean mayRezzIce() {
		return Step.APPROCHING_ICE == step && !this.ice.getIce().isRezzed();
	}

	public boolean mayRezzIce(Ice ice) {
		return mayRezzIce() && this.ice.getIce() == ice;
	}

	public boolean mayUseBreaker() {
		return Step.ENCOUTERING_ICE == step && this.ice.countUnbroken() > 0;
	}

	public boolean mayUseBreakerToBreak(IceBreaker breaker) {
		if (mayUseBreaker()) {
			Ice i = this.ice.getIce();
			return i.isBrokenBy(breaker) && i.hasEnoughtStrengthToBeBroke(breaker);
		}
		return false;
	}

	public Optional<EncounteredIce> getIce() {
		return Optional.ofNullable(ice);
	}

	/**
	 * Commence le run
	 */
	public void begin() {
		depth = server.icesCount();
		prepareApprochCurrentIce();
	}

	private void setStep(Step step) {
		this.step = step;
	}

	private void prepareApprochCurrentIce() {
		if (depth == 0) {
			setStep(Step.APPROCHING_SERVER);
			jackOffOr(this::prepareApprochServer);
		} else {

			if (!firstIce) {
				if (isMayJackOff()) {
					jackOffOr(this::doPrepareApprochCurrentIce);
					return;
				}
			}

			firstIce = false;
			doPrepareApprochCurrentIce();
		}
	}

	private void prepareApprochServer() {
		// evt de début d'approche de serveur
		game.apply(new ApprochingServerEvent(server), this::checkApprochServer);
	}

	private void jackOffOr(Flow onContinue) {

		Runner runner = game.getRunner();
		game.userContext(runner, "Would you like to jack-off ?", Type.POP_CARD);

		game.user(SimpleFeedback.noop(runner, runner, "Jack Off"), () -> {
			game.chat("{0} jacks off", runner);
			setStatus(Status.ENDED);
			clear();
		});
		game.user(SimpleFeedback.free(runner, runner, "Continue"), () -> {

			game.chat("The run continues");
			onContinue.apply();
		});
	}

	private void doPrepareApprochCurrentIce() {
		setStep(Step.APPROCHING_ICE);

		Ice ice = server.getIceAtHeight(depth);
		this.ice = new EncounteredIce(ice);

		// evt de début d'approche de glace
		game.apply(new ApprochingIceEvent(ice), this::approchCurrentIce);
	}

	private void approchCurrentIce() {
		// permet de rezzer la glace
		this.game.getTurn().pingpong(this::prepareEncounterCurrentIce);
	}

	private void prepareEncounterCurrentIce() {

		Ice i = this.ice.getIce();
		if (i.isRezzed()) {

			// on peut utiliser les icebreakers
			setStep(Step.ENCOUTERING_ICE);

			game.chat("{0} encouters {1}", game.getRunner(), i);

			// evenement de début de rencontre
			game.apply(new EncounteringIceEvent(this.ice), this::encounterCurrentIce);
		} else {
			game.chat("{0} doesn't rezz the ice", game.getCorp());
			passCurrentIce();
		}
	}

	private void encounterCurrentIce() {
		if (ice.isBypassed()) {
			passCurrentIce();
		} else {
			// ping pong pour breaker la glace
			game.getTurn().pingpong(this::afterIceEncountered);
		}

	}

	private void afterIceEncountered() {
		if (ice.isBypassed()) {
			passCurrentIce();
		} else {
			routinesToProcess = ice.getRoutines().stream().filter(ReadyedRoutine::isUnbroken).iterator();
			fireNextRoutine();
		}
	}

	private void fireNextRoutine() {

		endedOr(Optional.of(this::fireSubsCleareds), () -> {
			if (routinesToProcess.hasNext()) {
				ReadyedRoutine r = routinesToProcess.next();
				r.trigger(this, this::fireNextRoutine);
			} else
				passCurrentIce();
		});
	}

	/**
	 * Vérifie si le run est fini
	 * 
	 * @param onEnded
	 *            comportement supplémentaire si le run est fini
	 * @param other
	 *            comportement si le run n'es pas fini
	 */
	private void endedOr(Optional<Flow> onEnded, Flow other) {
		if (isEnded()) {
			onEnded.ifPresent(Flow::apply);
			clear();
		} else
			other.apply();
	}

	private boolean isEnded() {
		return Status.ENDED == status;
	}

	private void passCurrentIce() {
		fireSubsCleareds();

		this.ice = null;
		this.depth--;
		prepareApprochCurrentIce();
	}

	/**
	 * Envoi un evenement de nettoyage des routines
	 */
	private void fireSubsCleareds() {
		game.fire(new IceSubsClearedsEvent(this.ice.getIce()));
	}

	/**
	 * Vérifie que le run continu apres le fait d'avoir approché le server
	 */
	private void checkApprochServer() {

		endedOr(Optional.empty(), this::approchServer);

	}

	private void approchServer() {

		// TODO il faut demander le "plan daccès"

		clear();
	}

	/**
	 * Nettoyage du run
	 */
	private void clear() {

		if (isEnded())
			setStatus(Status.UNSUCCESFUL);

		cleared = true;
		game.fire(new RunStatusEvent(this));

		// on envoi la fin du run avec un evenement sur le run
		game.apply(new CleanupTheRun(this), next);

	}

	public Status getStatus() {
		return status;
	}

	public boolean isMayJackOff() {
		return mayJackOff;
	}

	public void setMayJackOff(boolean mayJackOff) {
		this.mayJackOff = mayJackOff;
	}

	public CorpServer getServer() {
		return server;
	}

	public void setServer(CorpServer server) {
		this.server = server;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public Flow getNext() {
		return next;
	}

	public boolean isCleared() {
		return cleared;
	}

	public boolean isSuccessful() {
		return Status.SUCCESFUL == status;
	}

	public boolean isFailed() {
		return Status.UNSUCCESFUL == status;
	}

	public Step getStep() {
		return step;
	}

	public Game getGame() {
		return game;
	}
}

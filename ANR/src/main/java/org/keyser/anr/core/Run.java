package org.keyser.anr.core;

import java.util.Iterator;
import java.util.Optional;

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
		return Step.APPROCHING_ICE == step;
	}

	public boolean mayRezzIce(Ice ice) {
		return mayRezzIce() && this.ice.getIce() == ice;
	}

	public boolean mayUseBreaker() {
		return Step.ENCOUTERING_ICE == step;
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
			// evt de début d'approche de serveur
			game.apply(new ApprochingServerEvent(server), this::approchServer);
		} else {
			setStep(Step.APPROCHING_ICE);

			Ice ice = server.getIceAtHeight(depth);
			this.ice = new EncounteredIce(ice);

			// evt de début d'approche de glace
			game.apply(new ApprochingIceEvent(ice), this::approchCurrentIce);
		}
	}

	private void approchCurrentIce() {
		// permet de rezzer la glace
		this.game.getTurn().pingpong(this::prepareEncounterCurrentIce);
	}

	private void prepareEncounterCurrentIce() {
		// on peut utiliser les icebreakers
		setStep(Step.ENCOUTERING_ICE);

		// evenement de début de rencontre
		game.apply(new EncounteringIceEvent(this.ice), this::encounterCurrentIce);
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
		if (isEnded()) {
			clear();
		} else {
			if (routinesToProcess.hasNext()) {
				ReadyedRoutine r = routinesToProcess.next();
				r.trigger(this, this::fireNextRoutine);
			} else
				passCurrentIce();
		}
	}

	private boolean isEnded() {
		return Status.ENDED == status;
	}

	private void passCurrentIce() {
		this.ice = null;
		this.depth--;
		prepareApprochCurrentIce();
	}

	private void approchServer() {

		// TODO gestion des carte à accéder

		clear();
	}

	/**
	 * Nettoyage du run
	 */
	private void clear() {

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
}

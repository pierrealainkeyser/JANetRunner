package org.keyser.anr.core;

import java.util.Collection;
import java.util.Iterator;

import org.keyser.anr.core.Game.PingPong;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.Routine;

public class Run extends AbstractGameContent implements Flow {

	class AbstractJackOffPossibilty extends RunEvent {
		private boolean mayJackOff = true;

		public boolean isMayJackOff() {
			return mayJackOff;
		}

		public void setMayJackOff(boolean mayJackOff) {
			this.mayJackOff = mayJackOff;
		}
	}

	/**
	 * Nettoyage technique de fin de run
	 * 
	 * @author PAF
	 * 
	 */
	public class CleanTheRunEvent extends RunEvent {
	}

	/**
	 * Nettoyage technique apres la glace
	 * 
	 * @author PAF
	 * 
	 */
	public class CleanUpIceEncounterEvent extends RunEvent {

	}

	/**
	 * La glace est rencontr�
	 * 
	 * @author PAF
	 * 
	 */
	public class IceIsEncounterEvent extends RunEvent {
	}

	/**
	 * La glace est pass�e
	 * 
	 * @author PAF
	 * 
	 */
	public class IceIsPassedEvent extends RunEvent {
	}

	public class JackOffBeforeIceEvent extends AbstractJackOffPossibilty {
	}

	public class JackOffBeforeServerEvent extends AbstractJackOffPossibilty {
	}

	public enum RunCondition {
		ENDED_BY_ROUTINE, IN_PROGRESS, JACKED_OUT, SUCCESSFUL;
	}

	public class RunEvent extends Event {

		public final Run getRun() {
			return Run.this;
		}

		public final EncounteredIce getIce() {
			return ice;
		}

	}

	public class RunIsFailledEvent extends RunEvent {
	}

	public class RunIsSuccessfulEvent extends RunEvent {
	}

	public enum RunOption {
		PAID, PAID_ICEBREAKER, PAID_REZZ, PAID_REZZ_ICE
	}

	public class SetupTheRunEvent extends RunEvent {
	}

	private RunCondition condition;

	private EncounteredIce ice;

	private boolean firstApprochedIce = true;

	private final FlowControler flow;

	private int heigth;

	private RunOption option;

	private CorpServer target;

	public Run(CorpServer target, FlowControler flow) {
		this.flow = flow;
		this.target = target;
		this.heigth = target.getIces().size();
		this.condition = RunCondition.IN_PROGRESS;
	}

	/**
	 * Si on peut utiliser des brises glace
	 * 
	 * @return
	 */
	public boolean mayUseIceBreaker() {
		return RunOption.PAID_ICEBREAKER == option;
	}

	/**
	 * Si on peut utiliser activer des glaces
	 * 
	 * @return
	 */
	public boolean mayRezzIce() {
		return RunOption.PAID_REZZ_ICE == option;
	}

	/**
	 * Si on peut utiliser activer des cartes (hors glace)
	 * 
	 * @return
	 */
	public boolean mayRezz() {
		return RunOption.PAID_REZZ_ICE == option || RunOption.PAID_REZZ == option;
	}

	@Override
	public void apply() {
		notification(NotificationEvent.START_OF_RUN.apply().m(this));
		apply(new SetupTheRunEvent(), this::startApproching);
	}

	private <T extends Event> void apply(T t, Flow to) {
		flow.apply(t, () -> {
			if (isFailed())
				failTheRun();
			else
				to.apply();
		});
	}

	private <T extends Event> void apply(T t, FlowArg<T> to) {
		flow.apply(t, (T ret) -> {
			if (isFailed())
				failTheRun();
			else
				to.apply(ret);
		});
	}

	/**
	 * Phase 2)
	 */
	private void startApproching() {
		option = RunOption.PAID;
		if (heigth > 0) {
			ice = new EncounteredIce(target.getIceAtHeight(heigth));
			
			///on envoie la notification
			notification(NotificationEvent.APPROCHING_ICE.apply().m(this));

			// phase 2.1
			pingpong(this::jackOffOrApprocheIce).runner();

		} else {
			startServerApproching();

		}
	}

	/**
	 * Phase 2.3)
	 * 
	 * @param event
	 */
	private void activateIce() {

		// on peut activer ou non
		option = RunOption.PAID_REZZ_ICE;

		// vers la phase 2.4
		pingpong(this::toEncounter).corp();
	}

	/**
	 * Phase 2.3)
	 */
	private void toEncounter() {
		if (ice.isBypassed() || !ice.isRezzed())
			apply(new IceIsPassedEvent(), this::toNextIce);
		else
			apply(new IceIsEncounterEvent(), this::checkEncountedIce);

	}

	/**
	 * Phase 2.4)
	 */
	private void checkEncountedIce() {

		// peut �tre pass� avec des effets genre femme fatale
		if (ice.isBypassed())
			apply(new IceIsPassedEvent(), this::toNextIce);
		else {
			option = RunOption.PAID_ICEBREAKER;

			// phase 3.1
			pingpong(this::checkRoutines).runner();
		}
	}

	/**
	 * Phase 3.2)
	 */
	private void checkRoutines() {
		UnbrokenRoutineCheck unbrokens = new UnbrokenRoutineCheck(ice.getToBeBrokens());
		unbrokens.apply();
	}

	/**
	 * V�rification des routines
	 * 
	 * @author PAF
	 * 
	 */
	private class UnbrokenRoutineCheck implements Flow {
		private final Iterator<Routine> it;

		private UnbrokenRoutineCheck(Collection<Routine> unbrokens) {
			this.it = unbrokens.iterator();
		}

		@Override
		public void apply() {
			// on déclenche la condition
			if (it.hasNext())
				it.next().trigger(Run.this, this);
			else
				passAfterEncounter();

		}
	}

	/**
	 * Permet de passer apres la rencontre
	 */
	private void passAfterEncounter() {
		apply(new IceIsPassedEvent(), this::cleanUpIceEncounter);
	}

	/**
	 * Nettoyage technique
	 */
	private void cleanUpIceEncounter() {
		apply(new CleanUpIceEncounterEvent(), this::toNextIce);
	}

	/**
	 * Nettoyage du run
	 */
	private void cleanUpTheRun() {
		flow.apply(new CleanTheRunEvent(), () -> {
			notification(NotificationEvent.END_OF_RUN.apply().m(this));

			// on finit le flux
				flow.apply();
			});
	}

	public void endedByRoutine() {
		this.condition = RunCondition.ENDED_BY_ROUTINE;
	}

	/**
	 * Le run � �chou�
	 */
	private void failTheRun() {
		// fin du run
		flow.apply(new RunIsFailledEvent(), this::cleanUpTheRun);
	}

	/**
	 * Phase 4)
	 */
	private void startServerApproching() {
		option = RunOption.PAID;

		// phase 4.1
		pingpong(this::jackOffOrApprocheServer).runner();
	}

	public boolean isFailed() {
		return RunCondition.JACKED_OUT == condition || RunCondition.ENDED_BY_ROUTINE == condition;
	}

	public boolean isSuccessful() {
		return RunCondition.SUCCESSFUL == condition;
	}

	private void jackOffDecision(AbstractJackOffPossibilty jackOff, Flow next) {
		if (jackOff.isMayJackOff()) {
			// le runner peut débrancher
			Question q = ask(Player.RUNNER, NotificationEvent.WANT_TO_JACKOFF);
			q.ask("jack-off").to(this::failTheRun);
			q.ask("continue-the-run").to(next);
			q.fire();

		} else
			next.apply();

	}

	/**
	 * Phase 2.1
	 */
	private void jackOffOrApprocheIce() {

		if (firstApprochedIce) {
			firstApprochedIce = false;
			activateIce();
		} else {

			// on approche de la glace le runner peut d�brancher
			apply(new JackOffBeforeIceEvent(), (j) -> jackOffDecision(j, this::activateIce));
		}
	}

	/**
	 * Phase 4.3
	 */
	private void afterNotJackingOffOnServer() {
		option = RunOption.PAID_REZZ;
		condition = RunCondition.SUCCESSFUL;
		pingpong(() -> apply(new RunIsSuccessfulEvent(), this::accessPhase)).runner();
	}

	/**
	 * Phase 4.5
	 */
	private void accessPhase() {
		// TODO gestion de la phase d'access

		cleanUpTheRun();
	}

	/**
	 * Phase 4.2
	 */
	private void jackOffOrApprocheServer() {
		
		notification(NotificationEvent.APPROCHING_SERVER.apply().m(this));
		
		// on approche de la glace le runner peut d�brancher
		apply(new JackOffBeforeServerEvent(), (j) -> jackOffDecision(j, this::afterNotJackingOffOnServer));

	}

	/**
	 * Permet de se d�brancher
	 */
	public void jackout() {
		this.condition = RunCondition.JACKED_OUT;
	}

	private PingPong pingpong(Flow next) {
		return getGame().pingpong(next);
	}

	/**
	 * On diminue la distance
	 */
	private void toNextIce() {
		--heigth;
		startApproching();
	}

	public EncounteredIce getEncounter() {
		return ice;
	}

	public CorpServer getTarget() {
		return target;
	}

}

package org.keyser.anr.core;

public class Run {

	public static enum Status {
		SUCCESFUL, UNSUCCESFUL, UNKNOW, IN_PROGRESS;
	}

	private Status status;

	public Status getStatus() {
		return status;
	}

}

// extends AbstractGameContent implements Flow {
//
// class AbstractJackOffPossibilty extends RunEvent {
// private boolean mayJackOff = true;
//
// public boolean isMayJackOff() {
// return mayJackOff;
// }
//
// public void setMayJackOff(boolean mayJackOff) {
// this.mayJackOff = mayJackOff;
// }
// }
//
// /**
// * Nettoyage technique de fin de run
// *
// * @author PAF
// *
// */
// public class CleanTheRunEvent extends RunEvent {
// }
//
// /**
// * Nettoyage technique apres la glace
// *
// * @author PAF
// *
// */
// public class CleanUpIceEncounterEvent extends RunEvent {
//
// }
//
// /**
// * La glace est rencontre
// *
// * @author PAF
// *
// */
// public class IceIsEncounterEvent extends RunEvent implements CardAccess{
//
// @Override
// public Card getCard() {
// return getIce().getIce();
// }
// }
//
// /**
// * La glace est pass�e
// *
// * @author PAF
// *
// */
// public class IceIsPassedEvent extends RunEvent {
// }
//
// public class JackOffBeforeIceEvent extends AbstractJackOffPossibilty {
// }
//
// public class JackOffBeforeServerEvent extends AbstractJackOffPossibilty {
// }
//
// public enum RunCondition {
// ENDED_BY_ROUTINE, IN_PROGRESS, JACKED_OUT, SUCCESSFUL;
// }
//
// public class RunEvent extends Event {
//
// public final Run getRun() {
// return Run.this;
// }
//
// public final EncounteredIce getIce() {
// return ice;
// }
// }
//
// public class CardAccededEvent extends RunEvent implements CardAccess{
// private final CorpCard card;
//
// private Cost stealCost;
//
// private Cost trashCost;
//
// public CardAccededEvent(CorpCard card) {
// this.card = card;
// // on accede gratuite au carte
// if (card instanceof Agenda) {
// setStealCost(Cost.free());
// } else if (card instanceof TrashableCard) {
//
// // on peut trasher la carte pour le cout donnée
// TrashableCard tc = (TrashableCard) card;
// setTrashCost(tc.getTrashCost().clone());
// }
// }
//
// @Override
// public CorpCard getCard() {
// return card;
// }
//
// public Cost getStealCost() {
// return stealCost;
// }
//
// public void setStealCost(Cost stealCost) {
// this.stealCost = stealCost;
// }
//
// public Cost getTrashCost() {
// return trashCost;
// }
//
// public void setTrashCost(Cost trashCost) {
// this.trashCost = trashCost;
// }
// }
//
// public class RunIsFailledEvent extends RunEvent {
// }
//
// public class RunIsSuccessfulEvent extends RunEvent {
//
// private final CorpAccessSettings settings = new CorpAccessSettings();
//
// public CorpAccessSettings getCorpAccess() {
// return settings;
// }
// }
//
// public enum RunOption {
// PAID, PAID_ICEBREAKER, PAID_REZZ, PAID_REZZ_ICE
// }
//
// public class SetupTheRunEvent extends RunEvent {
// }
//
// private RunCondition condition;
//
// private EncounteredIce ice;
//
// private boolean firstApprochedIce = true;
//
// private final FlowControler flow;
//
// private int heigth;
//
// private RunOption option;
//
// private CorpServer target;
//
// public Run(CorpServer target, FlowControler flow) {
// this.flow = flow;
// this.target = target;
// this.heigth = target.getIces().size();
// this.condition = RunCondition.IN_PROGRESS;
// }
//
// /**
// * Si on peut utiliser des brises glace
// *
// * @return
// */
// public boolean mayUseIceBreaker() {
// return RunOption.PAID_ICEBREAKER == option;
// }
//
// /**
// * Si on peut utiliser activer des glaces
// *
// * @return
// */
// public boolean mayRezzIce() {
// return RunOption.PAID_REZZ_ICE == option;
// }
//
// /**
// * Si on peut utiliser activer des cartes (hors glace)
// *
// * @return
// */
// public boolean mayRezz() {
// return RunOption.PAID_REZZ_ICE == option || RunOption.PAID_REZZ == option;
// }
//
// @Override
// public void apply() {
// notification(NotificationEvent.START_OF_RUN.apply().m(this));
// apply(new SetupTheRunEvent(), this::startApproching);
// }
//
// private <T extends Event> void apply(T t, Flow to) {
// flow.apply(t, () -> {
// if (isFailed())
// failTheRun();
// else
// to.apply();
// });
// }
//
// private <T extends Event> void apply(T t, FlowArg<T> to) {
// flow.apply(t, (T ret) -> {
// if (isFailed())
// failTheRun();
// else
// to.apply(ret);
// });
// }
//
// /**
// * Phase 2)
// */
// private void startApproching() {
// ice = null;
// option = RunOption.PAID;
// if (heigth > 0) {
// ice = new EncounteredIce(target.getIceAtHeight(heigth));
//
// // /on envoie la notification
// notification(NotificationEvent.APPROCHING_ICE.apply().m(this));
//
// // phase 2.1
// pingpong(this::jackOffOrApprocheIce).runner();
//
// } else {
// startServerApproching();
//
// }
// }
//
// /**
// * Phase 2.3)
// *
// * @param event
// */
// private void activateIce() {
//
// // on peut activer ou non
// option = RunOption.PAID_REZZ_ICE;
//
// // vers la phase 2.4
// pingpong(this::toEncounter).corp();
// }
//
// /**
// * Phase 2.3)
// */
// private void toEncounter() {
// if (ice.isBypassed() || !ice.isRezzed())
// apply(new IceIsPassedEvent(), this::toNextIce);
// else
// apply(new IceIsEncounterEvent(), this::checkEncountedIce);
//
// }
//
// /**
// * Phase 2.4)
// */
// private void checkEncountedIce() {
//
// // peut �tre pass� avec des effets genre femme fatale
// if (ice.isBypassed())
// apply(new IceIsPassedEvent(), this::toNextIce);
// else {
// option = RunOption.PAID_ICEBREAKER;
//
// // phase 3.1
// pingpong(this::checkRoutines).runner();
// }
// }
//
// /**
// * Phase 3.2)
// */
// private void checkRoutines() {
// UnbrokenRoutineCheck unbrokens = new
// UnbrokenRoutineCheck(ice.getToBeBrokens());
// unbrokens.apply();
// }
//
// /**
// * V�rification des routines
// *
// * @author PAF
// *
// */
// private class UnbrokenRoutineCheck implements Flow {
// private final Iterator<Routine> it;
//
// private UnbrokenRoutineCheck(Collection<Routine> unbrokens) {
// this.it = unbrokens.iterator();
// }
//
// @Override
// public void apply() {
// // on déclenche la condition
// if (it.hasNext())
// it.next().trigger(Run.this, this);
// else
// passAfterEncounter();
//
// }
// }
//
// /**
// * Permet de passer apres la rencontre
// */
// private void passAfterEncounter() {
// apply(new IceIsPassedEvent(), this::cleanUpIceEncounter);
// }
//
// /**
// * Nettoyage technique
// */
// private void cleanUpIceEncounter() {
// apply(new CleanUpIceEncounterEvent(), this::toNextIce);
// }
//
// /**
// * Nettoyage du run
// */
// private void cleanUpTheRun() {
// flow.apply(new CleanTheRunEvent(), () -> {
// notification(NotificationEvent.END_OF_RUN.apply().m(this));
//
// // on finit le flux
// flow.apply();
// });
// }
//
// public void endedByRoutine() {
// this.condition = RunCondition.ENDED_BY_ROUTINE;
// }
//
// /**
// * Le run a échoué
// */
// private void failTheRun() {
// // fin du run
// flow.apply(new RunIsFailledEvent(), this::cleanUpTheRun);
// }
//
// /**
// * Phase 4)
// */
// private void startServerApproching() {
// option = RunOption.PAID;
//
// // phase 4.1
// pingpong(this::jackOffOrApprocheServer).runner();
// }
//
// public boolean isFailed() {
// return RunCondition.JACKED_OUT == condition || RunCondition.ENDED_BY_ROUTINE
// == condition;
// }
//
// public boolean isSuccessful() {
// return RunCondition.SUCCESSFUL == condition;
// }
//
// private void jackOffDecision(AbstractJackOffPossibilty jackOff, Flow next) {
// if (jackOff.isMayJackOff()) {
// // le runner peut débrancher
// Question q = ask(Player.RUNNER, NotificationEvent.WANT_TO_JACKOFF);
// q.ask("jack-off").to(this::failTheRun);
// q.ask("continue-the-run").to(next);
// q.fire();
//
// } else
// next.apply();
// }
//
// /**
// * Phase 2.1
// */
// private void jackOffOrApprocheIce() {
//
// if (firstApprochedIce) {
// firstApprochedIce = false;
// activateIce();
// } else {
//
// // on approche de la glace le runner peut d�brancher
// apply(new JackOffBeforeIceEvent(), (j) -> jackOffDecision(j,
// this::activateIce));
// }
// }
//
// /**
// * Phase 4.3
// */
// private void afterNotJackingOffOnServer() {
// option = RunOption.PAID_REZZ;
// condition = RunCondition.SUCCESSFUL;
// pingpong(() -> apply(new RunIsSuccessfulEvent(),
// this::accessPhase)).runner();
// }
//
// /**
// * Phase 4.5
// */
// private void accessPhase(RunIsSuccessfulEvent event) {
// CardAccessGroup accessed = target.getAccessedCards(event.getCorpAccess());
// if (accessed.needToSort()) {
// // TODO faire les choses autrements, successivement
// Question q = ask(Player.RUNNER, NotificationEvent.SORT_ON_ACCESS);
// q.ask("sort-accededs-cards").setContent(accessed).to(Integer[].class, ids ->
// {
// accessing(accessed.inOrder(asList(ids)));
// });
// q.fire();
// } else
// accessing(accessed.inOrder());
//
// }
//
// /**
// * Réalise l'accès au carte
// *
// * @param cards
// */
// private void accessing(List<CorpCard> cards) {
// recurse(cards.iterator(), this::triggerAccess, this::cleanUpTheRun);
// }
//
// /**
// * Acces à la carte
// *
// * @param card
// * @param next
// */
// private void triggerAccess(CorpCard card, Flow next) {
//
// // on accede en branchant la carte
// card.whileBound(() -> {
// apply(new CardAccededEvent(card), evt -> checkAccess(evt, next));
// });
// }
//
// /**
// * On vient d'acceder à la carte
// *
// * @param evt
// * @param next
// */
// private void checkAccess(CardAccededEvent evt, Flow next) {
// CorpCard c = evt.getCard();
// Cost trashCost = evt.getTrashCost();
//
// Wallet w = getGame().getRunner().getWallet();
// if (trashCost != null) {
// ThrashCardAction tca = new ThrashCardAction(c);
//
// if (w.isAffordable(trashCost, tca)) {
//
// Question q = ask(Player.RUNNER, NotificationEvent.TRASH_CARD);
// q.ask("trash-it", c).setCost(trashCost).to(() -> {
//
// // on consomme et on trashe
// w.consume(trashCost, tca);
// c.setRezzed(true);
// c.trash(next);
// });
// q.ask("dont-trash-it", c).to(next);
// q.fire();
// return;
// }
// } else {
// Cost stealCost = evt.getStealCost();
// if (stealCost != null) {
//
// StealAgendaAction sta = new StealAgendaAction((Agenda) c);
//
// // si gratuit, on est obligé de le voler, mais on le voit
// if (stealCost.isZero()) {
// Question q = ask(Player.RUNNER, NotificationEvent.STEAL_AGENDA);
// q.ask("steal-it", c).to(() -> stealAgenda(stealCost, sta, next));
// q.fire();
// } else {
// Question q = ask(Player.RUNNER, NotificationEvent.STEAL_AGENDA);
// q.ask("steal-it", c).setCost(stealCost).to(() -> stealAgenda(stealCost, sta,
// next));
// q.ask("dont-steal-it", c).to(next);
// q.fire();
// }
// return;
// }
// }
//
// Question q = ask(Player.RUNNER, NotificationEvent.SHOW_ACCESSED_CARD);
// q.ask("access-done", c).to(next);
// q.fire();
// }
//
// /**
// * Wollet l'agenda
// *
// * @param sta
// * @param next
// */
// private void stealAgenda(Cost stealCost, StealAgendaAction sta, Flow next) {
//
// Wallet w = getGame().getRunner().getWallet();
// w.consume(stealCost, sta);
// sta.getCard().steal(next);
// }
//
// /**
// * Phase 4.2
// */
// private void jackOffOrApprocheServer() {
//
// notification(NotificationEvent.APPROCHING_SERVER.apply().m(this));
//
// // on approche de la glace le runner peut débrancher
// apply(new JackOffBeforeServerEvent(), (j) -> jackOffDecision(j,
// this::afterNotJackingOffOnServer));
//
// }
//
// /**
// * Permet de se débrancher
// */
// public void jackout() {
// this.condition = RunCondition.JACKED_OUT;
// }
//
// private PingPong pingpong(Flow next) {
// return getGame().pingpong(next);
// }
//
// /**
// * On diminue la distance
// */
// private void toNextIce() {
// --heigth;
// startApproching();
// }
//
// public EncounteredIce getEncounter() {
// return ice;
// }
//
// public CorpServer getTarget() {
// return target;
// }
//
// }

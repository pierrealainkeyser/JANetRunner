package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AbstractCardList;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.TrashList;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionConfirmSelection;
import org.keyser.anr.core.UserActionSelectCard;
import org.keyser.anr.core.UserDragAction;
import org.keyser.anr.core.runner.IceBreaker;

public abstract class Ice extends AbstractCardCorp {

	private final List<Routine> routines = new ArrayList<>();

	protected Ice(int id, MetaCard meta) {
		super(id, meta);
	}

	protected final Ice addRoutine(Routine r) {
		routines.add(r);
		return this;
	}

	public final List<Routine> getRoutines() {
		return Collections.unmodifiableList(routines);
	}

	@Override
	protected IceMetaCard getMeta() {
		return (IceMetaCard) super.getMeta();
	}

	protected boolean isBrokenByIA() {
		return true;
	}

	/**
	 * Permet de savoir si la glace est cassable par le breaker
	 * 
	 * @param breaker
	 * @return
	 */
	public boolean isBrokenBy(IceBreaker breaker) {

		if (hasAnySubTypes(CardSubType.BARRIER) && breaker.hasAnySubTypes(CardSubType.FRACTER))
			return true;
		else if (hasAnySubTypes(CardSubType.CODEGATE) && breaker.hasAnySubTypes(CardSubType.DECODER))
			return true;
		else if (hasAnySubTypes(CardSubType.SENTRY) && breaker.hasAnySubTypes(CardSubType.KILLER))
			return true;

		return breaker.hasAnySubTypes(CardSubType.IA) && isBrokenByIA();
	}

	public int getBaseStrength() {
		return getMeta().getStrength();
	}

	/**
	 * Calcul dynamique de la force de la glace
	 */
	public int computeStrength() {
		DetermineIceStrengthEvent evt = new DetermineIceStrengthEvent(this);
		game.fire(evt);
		int cpt = evt.getComputed();
		int strength = getBaseStrength();
		return Math.max(cpt - strength, 0);
	}

	public boolean hasEnoughtStrengthToBeBroke(IceBreaker breaker) {
		int strength = breaker.computeStrength();
		int iceStrength = computeStrength();
		return strength >= iceStrength;
	}

	@Override
	public boolean isRezzable() {
		// la glace peut -être rezzée à certains moments clés
		Optional<Run> optRun = game.getTurn().getRun();
		if (optRun.isPresent()) {

			Run run = optRun.get();
			return run.mayRezzIce(this);
		}
		return false;
	}

	/**
	 * Permet de supprimer les cartes
	 * 
	 * @param action
	 * @param next
	 */
	private void installedOnServer(CorpServer selected, Flow next) {

		Corp corp = getCorp();
		AbstractCardList list = new AbstractCardList();
		selected.streamIces().forEach(list::add);

		if (!list.isEmpty()) {
			Game g = getGame();

			g.userContext(this, "Remove ice").setExpectedAt(selected.topIceLocation());

			// on rajoute l'action de confirmation
			UserActionConfirmSelection confirm = new UserActionConfirmSelection(corp, this);

			// calcul des couts variables
			PlayIceAction playIceAction = new PlayIceAction(this);
			int size = list.size();
			for (int i = 0; i <= size; ++i) {
				Cost cost = Cost.credit(size - i);
				boolean enabled = game.mayAfford(corp.getOwner(), new CostForAction(cost, playIceAction));
				confirm.addCost(cost, enabled);
			}

			g.user(confirm.applyArg((toRemove, n) -> removeAndInstall(selected, toRemove, n)), next);

			// on peut sélectionner les cartes
			list.forEach(c -> g.user(new UserActionSelectCard(corp, c)));
		} else
			removeAndInstall(selected, list, next);
	}

	@Override
	public void playFeedback(CollectHabilities hab) {
		CostForAction cost = new CostForAction(Cost.free().withAction(1), new PlayIceAction(this));
		Corp corp = getCorp();
		UserAction playOperation = new UserAction(corp, this, cost, "Install");
		hab.add(playOperation.spendAndApply(this::prepareInstall));

		UserDragAction<CorpServer> drag = new UserDragAction<>(corp, this, cost, CorpServer.class);
		corp.eachServers(cs -> drag.add(cs.getId(), cs.topIceLocation()));
		hab.add(drag.spendAndApplyArg(this::installedOnServer));

	}

	/**
	 * demande ou installer les cartes, et les cartes à desinstaller
	 * 
	 * @param next
	 */
	private void prepareInstall(Flow next) {
		Game g = getGame();
		g.userContext(this, "Choose a server");

		Corp corp = getCorp();
		Consumer<CorpServer> install = cs -> {
			g.user(new UserAction(corp, cs, null, "Install").apply((ua, n) -> installedOnServer(cs, n)), next);
		};
		corp.eachServers(install);
	}

	public void defaultPlayChat(CorpServer selected) {
		game.chat("{0} installs an ice on {1}", getCorp(), selected);
	}

	/**
	 * La liste des cartes sélectionnés par le client est transmise
	 * 
	 * @param selected
	 * @param toRemove
	 * @param next
	 */
	private void removeAndInstall(CorpServer selected, AbstractCardList toRemove, Flow next) {

		this.setInstalled(true);

		int icesCount = selected.icesCount();
		selected.addIce(this, icesCount);

		defaultPlayChat(selected);

		// on rajoute toutes les cates sélectionnées
		TrashList tl = new TrashList(TrashCause.OTHER_INSTALLED);
		toRemove.forEach(tl::add);

		// gestion du cout
		CostForAction cfa = new CostForAction(Cost.credit(icesCount - toRemove.size()), new PlayIceAction(this));
		getCorp().spend(cfa, () -> {
			// trash toutes les cartes
				tl.trash(next.wrap(this::cleanupInstall));
			});

	}

}

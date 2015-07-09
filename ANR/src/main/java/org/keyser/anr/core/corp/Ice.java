package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AbstractCardInstalledCleanup;
import org.keyser.anr.core.AbstractCardList;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.FeedbackWithArgs;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.TrashList;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionConfirmSelection;
import org.keyser.anr.core.UserActionContext.Type;
import org.keyser.anr.core.UserActionSelectCard;

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
	public boolean isRezzable() {
		// TODO la glace peut -être rezzée à certains moments clés
		return false;
	}

	/**
	 * Permet de supprimer les cartes
	 * 
	 * @param action
	 * @param next
	 */
	private void installedOnServer(UserAction action, Flow next) {

		Corp corp = getCorp();
		AbstractCardList list = new AbstractCardList();
		CorpServer selected = action.getServer();
		selected.forEachIce(list::add);

		if (!list.isEmpty()) {
			Game g = getGame();
			g.userContext(this, "Remove ice", Type.REMOVE_ON_INSTALL);

			// on rajoute l'action de confirmation
			UserActionConfirmSelection userAction = new UserActionConfirmSelection(corp, this);

			// calcul des couts variables
			PlayIceAction playIceAction = new PlayIceAction(this);
			int size = list.size();
			for (int i = 0; i < size; ++i) {
				Cost cost = Cost.credit(size - i - 1);
				boolean enabled = game.mayAfford(corp.getOwner(), new CostForAction(cost, playIceAction));
				userAction.addCost(cost, enabled);
			}

			g.user(new FeedbackWithArgs<>(userAction, (u, toRemove, n) -> removeAndInstall(selected, toRemove, n)), next);

			// on peut sélectionner les cartes
			list.forEach(c -> g.user(new UserActionSelectCard(corp, c)));
		} else
			removeAndInstall(selected, list, next);
	}

	@Override
	public void playFeedback(CollectHabilities hab) {
		UserAction playOperation = new UserAction(getCorp(), this, new CostForAction(Cost.free().withAction(1), new PlayIceAction(this)), "Install");
		hab.add(playOperation.spendAndApply(this::prepareInstall));
	}

	/**
	 * demande ou installer les cartes, et les cartes à desinstaller
	 * 
	 * @param next
	 */
	private void prepareInstall(Flow next) {
		Game g = getGame();
		g.userContext(this, "Choose a server", Type.INSTALL_IN_SERVER);

		Corp corp = getCorp();
		Consumer<CorpServer> install = cs -> {
			g.user(new UserAction(corp, cs, null, "Install").apply(this::installedOnServer), next);
		};
		corp.eachServers(install);
	}

	private void processCleanUp(Flow next) {

		// cleanup et poursuite du traitement
		getGame().apply(new AbstractCardInstalledCleanup(this), next);
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

		// TODO gestion du cout

		selected.addIce(this, selected.icesCount());

		// on rajoute toutes les cates sélectionnées
		TrashList tl = new TrashList(TrashCause.OTHER_INSTALLED);
		toRemove.forEach(tl::add);

		// trash toutes les cartes
		tl.trash(next.wrap(this::processCleanUp));
	}

}

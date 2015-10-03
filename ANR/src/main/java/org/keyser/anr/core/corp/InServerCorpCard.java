package org.keyser.anr.core.corp;

import java.util.function.Consumer;

import org.keyser.anr.core.AbstractCardContainer;
import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AbstractCardList;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.PlayCardAction;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.TrashList;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionConfirmSelection;
import org.keyser.anr.core.UserActionSelectCard;
import org.keyser.anr.core.UserDragAction;

public abstract class InServerCorpCard extends AbstractCardCorp {

	protected InServerCorpCard(int id, MetaCard meta) {
		super(id, meta);
	}

	protected abstract PlayCardAction<? extends AbstractCardCorp> playAction();

	@Override
	public void playFeedback(CollectHabilities hab) {
		CostForAction cost = new CostForAction(Cost.free().withAction(1), playAction());
		Corp corp = getCorp();
		UserAction playOperation = new UserAction(corp, this, cost, "Install");
		hab.add(playOperation.spendAndApply(this::prepareInstall));

		UserDragAction<CorpServer> drag = new UserDragAction<>(corp, this, cost, CorpServer.class);
		corp.eachServers(cs -> drag.add(cs.getId(), cs.containerFor(this).lastLocation()));
		hab.add(drag.spendAndApplyArg(this::installedOnServer));
	}

	/**
	 * Permet de tester le caractère installable
	 * 
	 * @param server
	 * @return
	 */
	protected boolean installableOn(CorpServer server) {
		return true;
	}

	protected CorpServer installedOn() {
		return getCorp().getOrCreate(getLocation().getServerIndex());
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
			if (installableOn(cs))
				g.user(new UserAction(corp, cs, null, "Install").apply((ua, n) -> installedOnServer(cs, n)), next);
		};
		corp.eachServers(install);
	}

	@Override
	protected void onRezzed(Flow next) {
		CorpServer installedOn = installedOn();
		installedOn.dispatchKnownUpgrades();

		// on rajoute toutes les cates sélectionnées
		TrashList tl = new TrashList(TrashCause.OTHER_INSTALLED);

		// on recherche toutes les cartes illegales sur le serveur
		installedOn.collectIllegalsCards(this, false, tl::add);

		// trash toutes les cartes
		tl.trash(next);
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
		selected.collectIllegalsCards(this, true, list::add);

		if (!list.isEmpty()) {
			Game g = getGame();
			g.userContext(this, "Remove others");

			// on rajoute l'action de confirmation
			UserActionConfirmSelection confirm = new UserActionConfirmSelection(corp, this);
			Cost free = Cost.free();
			list.forEach(c -> confirm.addCost(free, true));

			g.user(confirm.applyArg((toRemove, n) -> removeAndInstall(selected, toRemove, n)), next);

			// on peut sélectionner les cartes
			list.forEach(c -> g.user(new UserActionSelectCard(corp, c)));
		} else
			removeAndInstall(selected, list, next);

	}
	
	public void defaultPlayChat(CorpServer selected) {
		game.chat("{0} installs a card on {1}", getCorp(), selected);
	}

	/**
	 * La liste des cartes sélectionnés par le client est transmise
	 * 
	 * @param selected
	 * @param toRemove
	 * @param next
	 */

	@SuppressWarnings("unchecked")
	private void removeAndInstall(CorpServer selected, AbstractCardList toRemove, Flow next) {

		this.setInstalled(true);
		
		defaultPlayChat(selected);

		// installation dans la zone qui va bien
		((AbstractCardContainer<InServerCorpCard>) selected.containerFor(this)).add(this);

		// on rajoute toutes les cates sélectionnées
		TrashList tl = new TrashList(TrashCause.OTHER_INSTALLED);

		// on recherche toutes les cartes illegales sur le serveur
		selected.collectIllegalsCards(this, false, tl::add);
		toRemove.forEach(tl::add);

		// trash toutes les cartes
		tl.trash(next.wrap(this::cleanupInstall));
	}
}

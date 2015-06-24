package org.keyser.anr.core.corp;

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
import org.keyser.anr.core.PlayCardAction;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.TrashList;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionConfirmSelection;
import org.keyser.anr.core.UserActionContext.Type;
import org.keyser.anr.core.UserActionSelectCard;

public abstract class InServerCorpCard extends AbstractCardCorp {

	protected InServerCorpCard(int id, MetaCard meta) {
		super(id, meta);
	}

	protected abstract PlayCardAction<? extends AbstractCardCorp> playAction();

	@Override
	public void playFeedback(CollectHabilities hab) {
		UserAction playOperation = new UserAction(getCorp(), this, new CostForAction(Cost.free().withAction(1), playAction()), "Install");
		hab.add(playOperation.spendAndApply(this::prepareInstall));
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
			if (installableOn(cs))
				g.user(new UserAction(corp, cs, null, "Install").apply(this::installedOnServer), next);
		};
		corp.eachServers(install);
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
		selected.collectIllegalsCards(this, true, list::add);

		if (!list.isEmpty()) {
			Game g = getGame();
			g.userContext(this, "Remove assets, upgrades or agenda", Type.REMOVE_ON_INSTALL);

			// on rajoute l'action de confirmation
			g.user(new FeedbackWithArgs<>(new UserActionConfirmSelection(corp, this), (u, toRemove, n) -> removeAndInstall(selected, toRemove, n)), next);

			// on peut sélectionner les cartes
			list.forEach(c -> g.user(new UserActionSelectCard(corp, c)));
		} else
			removeAndInstall(selected, list, next);

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

		// installation dans la zone qui va bien
		if (selected instanceof CorpServerCentral)
			selected.addUpgrade((Upgrade) this);
		else
			selected.addAssetOrUpgrade(this);

		// on rajoute toutes les cates sélectionnées
		TrashList tl = new TrashList(TrashCause.OTHER_INSTALLED);

		// on recherche toutes les cartes illegales sur le serveur
		selected.collectIllegalsCards(this, false, tl::add);
		toRemove.forEach(tl::add);

		// trash toutes les cartes
		tl.trash(next.wrap(this::processCleanUp));
	}

	private void processCleanUp(Flow next) {

		// cleanup et poursuite du traitement
		getGame().apply(new AbstractCardInstalledCleanup(this), next);
	}

}

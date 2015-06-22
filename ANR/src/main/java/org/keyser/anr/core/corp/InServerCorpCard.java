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
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionArgs;
import org.keyser.anr.core.UserActionContext.Type;

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
			if (installableOn(cs)) {
				AbstractCardList list = new AbstractCardList();
				cs.forEachAssetOrUpgrades(list::add);
				UserActionArgs<AbstractCardList> ua = new UserActionArgs<>(corp, cs, null, "Install", list);
				g.user(new FeedbackWithArgs<>(ua, this::installed), next);
			}
		};
		corp.eachServers(install);
	}

	/**
	 * Permet de supprimer les cartes
	 * 
	 * @param action
	 * @param discard
	 * @param next
	 */
	private void installed(UserAction action, AbstractCardList discard, Flow next) {
		// TODO trash des cartes sélectionnés

		this.setInstalled(true);

		// installation dans la zone qui va bien
		CorpServer server = action.getServer();
		if (server instanceof CorpServerCentral)
			server.addUpgrade((Upgrade) this);
		else
			server.addAssetOrUpgrade(this);

		server.trashOtherIllegalsCards(this, next.wrap(this::processCleanUp));

	}

	private void processCleanUp(Flow next) {

		// cleanup et poursuite du traitement
		getGame().apply(new AbstractCardInstalledCleanup(this), next);
	}

}

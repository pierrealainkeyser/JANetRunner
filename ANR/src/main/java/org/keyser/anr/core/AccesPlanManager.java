package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.keyser.anr.core.UserActionContext.Type;
import org.keyser.anr.core.corp.CorpServer;

/**
 * Les cartes à accéder
 * 
 * @author PAF
 *
 */
public class AccesPlanManager {

	private LinkedList<AccesSingleCard> sequential = new LinkedList<>();

	private List<AccesSingleCard> unordered = new LinkedList<>();

	private List<AbstractCardCorp> accededs = new LinkedList<>();

	private final Game game;

	private final Flow next;

	public AccesPlanManager(Game game, Flow next) {
		this.game = game;
		this.next = next;
	}

	public void commit() {
		selectNextCardToAccess();
	}

	private void selectNextCardToAccess() {
		List<AccesSingleCard> accessibles = getAccessibles();

		if (accessibles.isEmpty()) {
			// fin des acces
			next.apply();
		} else {
			Runner runner = game.getRunner();
			game.userContext(runner, "Choose cards to access", Type.POP_CARD);

			for (AccesSingleCard accessible : accessibles) {
				Flow commit = () -> prepareAccessCard(accessible);

				CorpServer serverSource = accessible.getServerSource();
				if (serverSource == null)
					game.user(SimpleFeedback.free(runner, accessible.getAcceded(), "Access"), commit);
				else
					game.user(SimpleFeedback.free(runner, serverSource, "Access a card"), commit);
			}
		}
	}

	private void prepareAccessCard(AccesSingleCard accessed) {

		// déplacement dans la zone d'acces
		AbstractCardCorp card = accessed.getAcceded();
		card.setVisible(true);
		card.setLocation(getNextAcceded());

		// TODO suite de l'accès
		selectNextCardToAccess();

	}

	/**
	 * Renvoi la position de la prochain carte accédée
	 * 
	 * @return
	 */
	public CardLocation getNextAcceded() {
		return CardLocation.accedeed(accededs.size());
	}

	public void addUnordered(AbstractCardCorp card) {
		unordered.add(new AccesSingleCard(card));
	}

	public void addSequential(AbstractCardCorp card) {
		sequential.add(new AccesSingleCard(card));
	}

	public void addSequential(AbstractCardCorp card, CorpServer serverSource) {
		sequential.add(new AccesSingleCard(card, serverSource));
	}

	public void access(AccesSingleCard card) {
		sequential.remove(card);
		unordered.remove(card);
		accededs.add(card.getAcceded());
	}

	private List<AccesSingleCard> getAccessibles() {
		List<AccesSingleCard> accessibles = new ArrayList<>();
		accessibles.addAll(unordered);
		if (!sequential.isEmpty())
			accessibles.add(sequential.getFirst());

		return accessibles;
	}
}

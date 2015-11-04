package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

	public List<AccesSingleCard> getAccessibles() {
		List<AccesSingleCard> accessibles = new ArrayList<>();
		accessibles.addAll(unordered);
		if (!sequential.isEmpty())
			accessibles.add(sequential.getFirst());

		return accessibles;
	}
}

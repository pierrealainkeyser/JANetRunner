package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Un group d'acces au carte d'un serveur
 * 
 * @author PAF
 * 
 */
public class CardAccessGroup {

	private final List<CorpCard> anyOrder = new ArrayList<>();

	private final List<CorpCard> sequential = new ArrayList<>();

	public void add(CorpCard c) {
		anyOrder.add(c);
	}

	public List<CorpCard> inOrder() {
		return sequential;
	}

	/**
	 * Renvoi la liste des cartes accéder dans l'ordre
	 * 
	 * @param ids
	 * @return
	 */
	public List<CorpCard> inOrder(List<Integer> ids) {
		List<CorpCard> c = new ArrayList<>(ids.size());
		ids.forEach(id -> c.add(find(id)));
		return c;
	}

	/**
	 * Permet de trouver une carte dans une des collections
	 * 
	 * @param cardId
	 * @return
	 */
	private CorpCard find(int cardId) {
		Predicate<CorpCard> match = c -> c.getId() == cardId;
		Optional<CorpCard> o = anyOrder.stream().filter(match).findFirst();
		if (o.isPresent())
			return o.get();
		return sequential.stream().filter(match).findFirst().get();
	}

	public void addSequential(CorpCard c) {
		sequential.add(c);
	}

	/**
	 * Return vrai s'il faut trie
	 * 
	 * @return
	 */
	public boolean needToSort() {
		return anyOrder.size() > 1;
	}
}

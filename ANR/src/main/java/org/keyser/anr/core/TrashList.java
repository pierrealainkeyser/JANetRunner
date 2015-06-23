package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Permet de trasher une liste de carte
 * 
 * @author PAF
 *
 */
public class TrashList {

	private final List<AbstractCard> cards = new ArrayList<>();

	private final TrashCause cause;

	public TrashList(TrashCause cause) {
		super();
		this.cause = cause;
	}

	public TrashList add(AbstractCard c) {
		this.cards.add(c);
		return this;
	}

	/**
	 * Trash toutes les cartes
	 * @param next
	 */
	public void trash(Flow next) {
		RecursiveIterator.recurse(cards.iterator(), this::doTrash, next);
	}

	private void doTrash(AbstractCard ac, Flow next) {
		ac.trash(cause, next);
	}
}

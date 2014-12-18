package org.keyser.anr.core;

/**
 * Les actions de base du joueur
 * 
 * @author pakeyser
 *
 * @param <T>
 */
public abstract class PlayCardAction<T extends AbstractCard> extends
		AbstractCardAction<T> {

	protected PlayCardAction(T card) {
		super(card);
	}

}

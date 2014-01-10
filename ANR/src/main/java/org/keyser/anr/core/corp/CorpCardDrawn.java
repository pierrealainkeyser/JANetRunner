package org.keyser.anr.core.corp;

/**
 * L'evenement la Corp a piocher
 * 
 * @author PAF
 * 
 */
public class CorpCardDrawn extends CorpCardEvent {
	public CorpCardDrawn(CorpCard card) {
		super(card);
	}
}
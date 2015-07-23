package org.keyser.anr.core;

import java.io.Serializable;

/**
 * Permet de sauvegarder l'état
 * 
 * @author pakeyser
 *
 */
public class CoolEffectMemento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3499636493215282067L;
	private Class<? extends CoolEffect> type;

	private int cardId;

	public CoolEffectMemento() {
	}

	public CoolEffectMemento(CoolEffect ce) {
		this.type = ce.getClass();
		this.cardId = ce.getSource().getId();
	}

	public Class<? extends CoolEffect> getType() {
		return type;
	}

	public void setType(Class<? extends CoolEffect> type) {
		this.type = type;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

}

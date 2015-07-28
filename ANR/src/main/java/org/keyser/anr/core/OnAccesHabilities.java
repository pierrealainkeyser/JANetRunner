package org.keyser.anr.core;

/**
 * Gestion de l'accès. On commence normalement par la corp
 * 
 * @author pakeyser
 *
 */
public class OnAccesHabilities extends CollectHabilities {

	private final AbstractCardCorp acceded;

	private boolean revealToCorp = false;

	public OnAccesHabilities(AbstractCardCorp acceded, PlayerType type) {
		super(type);
		this.acceded = acceded;
	}

	public OnAccesHabilities revealToCorp() {
		revealToCorp = true;
		return this;
	}

	public AbstractCardCorp getAcceded() {
		return acceded;
	}

	public boolean isRevealToCorp() {
		return revealToCorp;
	}

}

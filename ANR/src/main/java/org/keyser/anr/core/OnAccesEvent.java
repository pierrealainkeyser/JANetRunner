package org.keyser.anr.core;

/**
 * Gestion de l'accés. On commence normalement par la corp
 * 
 * @author pakeyser
 *
 */
public class OnAccesEvent extends AbstractCardEvent {

	private boolean continueAccess = true;

	public OnAccesEvent(AbstractCardCorp acceded) {
		super(acceded, null);
	}

	public OnAccesEvent context() {
		getGame().userContext(getPrimary(), "Card acceded", UserActionContext.Type.POP_CARD);
		return this;
	}

	public OnAccesEvent stopAccess() {
		continueAccess = false;
		return this;
	}

	@Override
	public AbstractCardCorp getPrimary() {
		return (AbstractCardCorp) super.getPrimary();
	}

	public boolean isContinueAccess() {
		return continueAccess;
	}

}

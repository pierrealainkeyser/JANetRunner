package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.PlayCardAction;

public abstract class Upgrade extends InServerCorpCard {

	protected Upgrade(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected PlayCardAction<? extends AbstractCardCorp> playAction() {
		return new PlayUpgradeAction(this);
	}
	
	@Override
	public boolean isRezzable() {
		return true;
	}

}

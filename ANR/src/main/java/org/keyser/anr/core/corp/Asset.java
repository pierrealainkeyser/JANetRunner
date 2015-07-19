package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.PlayCardAction;


public abstract class Asset extends AssetOrAgenda {
	protected Asset(int id, MetaCard meta) {
		super(id, meta);
	}

	public Cost getThrashCost() {
		return ((AssetUpgradeMetaCard) getMeta()).getCost();
	}

	@Override
	protected PlayCardAction<? extends AbstractCardCorp> playAction() {
		return new PlayAssetAction(this);
	}

	@Override
	public boolean isRezzable() {
		return true;
	}

}

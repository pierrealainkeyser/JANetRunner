package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.PlayCardAction;


public abstract class Asset extends AssetOrAgenda implements Trashable{
	protected Asset(int id, MetaCard meta) {
		super(id, meta);
	}
	
	@Override
	protected AssetUpgradeMetaCard getMeta() {
		return (AssetUpgradeMetaCard)super.getMeta();
	}

	@Override
	public Cost getThrashCost() {
		return getMeta().getCost();
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

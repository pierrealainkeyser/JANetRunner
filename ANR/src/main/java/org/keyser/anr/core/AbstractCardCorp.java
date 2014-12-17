package org.keyser.anr.core;

public class AbstractCardCorp extends AbstractCard {

	protected AbstractCardCorp(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.CORP, CardLocation::isInCorpHand);
	}
	
	@Override
	public PlayerType getOwner() {
		return PlayerType.CORP;
	}
}

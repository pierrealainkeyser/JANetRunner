package org.keyser.anr.core.corp;

import org.keyser.anr.core.MetaCard;

public abstract class AssetOrAgenda extends InServerCorpCard {

	public AssetOrAgenda(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected boolean installableOn(CorpServer server) {
		return !(server instanceof CorpServerCentral);
	}

}
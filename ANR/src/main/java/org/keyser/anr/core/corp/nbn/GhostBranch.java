package org.keyser.anr.core.corp.nbn;

import static java.util.Arrays.asList;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Asset;
import org.keyser.anr.core.corp.AssetUpgradeMetaCard;

public class GhostBranch extends Asset {
	
	public final static AssetUpgradeMetaCard INSTANCE = new AssetUpgradeMetaCard("Ghost Branch", Faction.NBN.infl(1), Cost.credit(0), Cost.credit(0), false, "01087", asList(CardSubType.AMBUSH), GhostBranch::new);
	

	protected GhostBranch(int id, MetaCard meta) {
		super(id, meta);
	}



	@Override
	public boolean isAdvanceable() {
		return true;
	}
}

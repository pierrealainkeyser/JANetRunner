package org.keyser.anr.core.corp.nbn;

import static java.util.Arrays.asList;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.AssetUpgradeMetaCard;
import org.keyser.anr.core.corp.Upgrade;

public class RedHerrings extends Upgrade {

	public final static MetaCard INSTANCE = new AssetUpgradeMetaCard("Red Herrings", Faction.NBN.infl(2), Cost.credit(1), Cost.credit(1), false, "01091", asList(),
			RedHerrings::new);

	protected RedHerrings(int id, MetaCard meta) {
		super(id, meta);
	}

	
	
}

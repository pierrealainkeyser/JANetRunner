package org.keyser.anr.core.corp.nbn;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Operation;

public class AnonymousTip extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Anonymous Tip", Faction.NBN.infl(1), Cost.credit(0), false, "01083", emptyList(), AnonymousTip::new);

	protected AnonymousTip(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void invoke(Flow next) {
		// TODO notification de l'effet ?
		getCorp().draw(3, next);

	}
}

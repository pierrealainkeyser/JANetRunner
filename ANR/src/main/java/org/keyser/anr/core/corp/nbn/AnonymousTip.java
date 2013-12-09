package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "Anonymous Tip", oid = "01083")
public class AnonymousTip extends Operation {
	public AnonymousTip() {
		super(Faction.NBN.infl(1), Cost.free());
	}

	@Override
	public void apply(Flow next) {
		trash();
		getGame().getCorp().draw(3, next);
		
	}
}

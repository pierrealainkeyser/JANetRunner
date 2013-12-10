package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "Psychographics", oid = "01085")
public class Psychographics extends Operation {
	public Psychographics() {
		super(Faction.NBN.infl(3), Cost.free());
	}
	
	@Override
	public boolean isEnabled() {
		return getGame().getRunner().isTagged();
	}
	
	@Override
	public void apply(Flow next) {
		//TODO
		next.apply();
		
	}
}

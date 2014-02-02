package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.TraceAction;
import org.keyser.anr.core.corp.Operation;
import org.keyser.anr.core.runner.AddTagsEvent;

@CardDef(name = "SEA Source", oid = "01086")
public class SEASource extends Operation {
	public SEASource() {
		super(Faction.NBN.infl(2), Cost.credit(2));
	}

	@Override
	public boolean isEnabled() {
		return getGame().runnerHasRunedLastTurn();
	}

	@Override
	public void apply(Flow next) {

		TraceAction ta = new TraceAction("Trace[3] If successful, give the Runner 1 tag", 3, this::handle);
		ta.apply(getGame(), next);
	}

	private void handle(TraceAction ta, Flow next) {
		if (ta.isSucessful()) {
			AddTagsEvent evt = new AddTagsEvent(1);
			evt.fire(getGame(), next);
		} else
			next.apply();

	}
}

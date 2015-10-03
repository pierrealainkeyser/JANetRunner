package org.keyser.anr.core.corp.nbn;

import static java.util.Collections.emptyList;

import java.util.function.Predicate;

import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Operation;
import org.keyser.anr.core.corp.TraceAction;
import org.keyser.anr.core.runner.AddTagsEvent;

public class SEASource extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("SEA Source", Faction.NBN.infl(2), Cost.credit(2), false, "01086", emptyList(), SEASource::new);

	protected SEASource(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected Predicate<CollectHabilities> customizePlayPredicate(Predicate<CollectHabilities> pred) {
		pred = pred.and(previousTurn(t -> t.anyRun(Run.Status.SUCCESFUL)));
		return pred;
	}

	@Override
	protected void invoke(UserAction ua, Flow next) {

		defaultPlayChat();
		next.apply();

	}

	private void handle(TraceAction ta, Flow next) {
		if (true) {
			AddTagsEvent evt = new AddTagsEvent(this, 1);
			evt.fire(next);
		} else
			next.apply();

	}
}

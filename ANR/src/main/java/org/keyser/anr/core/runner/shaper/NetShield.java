package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.function.Predicate;

import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.runner.AddDamageEvent;
import org.keyser.anr.core.runner.Program;
import org.keyser.anr.core.runner.ProgramMetaCard;
import org.keyser.anr.core.runner.UseProgramAction;

public class NetShield extends Program {

	public final static ProgramMetaCard INSTANCE = new ProgramMetaCard("Net Shield", SHAPER.infl(1), credit(2), false, "01033", 1, emptyList(), NetShield::new);

	public NetShield(int id, MetaCard meta) {
		super(id, (ProgramMetaCard) meta);

		match(AddDamageEvent.class, emb -> {

			Predicate<AddDamageEvent> pred = installed();
			pred = pred.and(ade -> ade.isNetDamage() && ade.getDamage() > 0);
			pred = pred.and(affordable(credit(1), new UseProgramAction(NetShield.this)));
			emb.test(pred);

		});
	}
}

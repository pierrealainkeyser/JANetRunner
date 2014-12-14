package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.function.Predicate;

import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.Feedback;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.runner.DoDamageEvent;
import org.keyser.anr.core.runner.FlatDamagePreventionAction;
import org.keyser.anr.core.runner.Program;
import org.keyser.anr.core.runner.ProgramMetaCard;
import org.keyser.anr.core.runner.RunnerPreventibleEffect;
import org.keyser.anr.core.runner.UseProgramAction;

public class NetShield extends Program {

	public final static ProgramMetaCard INSTANCE = new ProgramMetaCard("Net Shield", SHAPER.infl(1), credit(2), false, "01033", 1, emptyList(), NetShield::new);

	protected NetShield(int id, MetaCard meta) {
		super(id, (ProgramMetaCard) meta);

		match(DoDamageEvent.class, emb -> doDamageEvent(emb));
	}

	private void doDamageEvent(EventMatcherBuilder<DoDamageEvent> emb) {

		// TODO ce n'est que pour le premier net dommage du tour. Il faut
		// consigner les effets appliquer

		Predicate<DoDamageEvent> pred = installed();
		pred = pred.and(ade -> ade.isNetDamage() && ade.getAmount() > 0);
		emb.test(pred);

		emb.call(evt -> evt.register(this::createFeedback));
	}

	private Feedback<?, ?> createFeedback(RunnerPreventibleEffect event) {
		FlatDamagePreventionAction prevent = new FlatDamagePreventionAction(this, new CostForAction(credit(1), new UseProgramAction(this)), "Prevent 1 Net", 1);
		return prevent.feedback(event);
	}
}

package org.keyser.anr.core.corp.jinteki;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.JINTEKI;

import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Operation;
import org.keyser.anr.core.runner.DoDamageEvent;
import org.keyser.anr.core.runner.DoDamageEvent.DamageType;

public class NeuralEMP extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Neural EMP", JINTEKI.infl(2), credit(2), false, "01072", emptyList(), NeuralEMP::new);

	protected NeuralEMP(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void invoke(Flow next) {
		new DoDamageEvent(this, "Neural EMP", 1, DamageType.NET).fire(next);
	}
}

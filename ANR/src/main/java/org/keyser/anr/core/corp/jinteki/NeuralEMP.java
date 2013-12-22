package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "Neural EMP", oid = "01072")
public class NeuralEMP extends Operation {
	public NeuralEMP() {
		super(Faction.JINTEKI.infl(2), Cost.credit(2));
	}

	@Override
	public void apply(Flow next) {
	}
}

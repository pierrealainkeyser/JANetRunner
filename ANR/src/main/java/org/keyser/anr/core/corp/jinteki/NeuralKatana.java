package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Neural Katana", oid = "01077")
public class NeuralKatana extends Ice {
	public NeuralKatana() {
		super(Faction.JINTEKI.infl(2), Cost.credit(4), 3, CardSubType.SENTRY);
	}
}

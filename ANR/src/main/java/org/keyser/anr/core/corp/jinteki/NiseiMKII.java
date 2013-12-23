package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;

@CardDef(name = "Nisei MK II", oid = "01068")
public class NiseiMKII extends Agenda {
	public NiseiMKII() {
		super(Faction.JINTEKI, 2, 4);
	}
}

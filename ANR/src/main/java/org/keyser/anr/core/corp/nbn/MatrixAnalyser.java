package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceType;

@CardDef(name = "Matrix Analyser", oid = "01090")
public class MatrixAnalyser extends Ice {

	public MatrixAnalyser() {
		super(NBN.infl(2), credit(1), IceType.SENTRY, 3);
	}
}

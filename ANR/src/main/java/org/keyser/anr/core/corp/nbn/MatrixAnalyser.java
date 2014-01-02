package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Matrix Analyzer", oid = "01089")
public class MatrixAnalyser extends Ice {

	public MatrixAnalyser() {
		super(NBN.infl(2), credit(1), 3, CardSubType.SENTRY);
	}
}

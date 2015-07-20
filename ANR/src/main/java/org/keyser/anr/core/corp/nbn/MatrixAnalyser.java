package org.keyser.anr.core.corp.nbn;

import static java.util.Arrays.asList;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceMetaCard;

public class MatrixAnalyser extends Ice {

	public static final MetaCard INSTANCE = new IceMetaCard("Matrix Analyzer", NBN.infl(2), Cost.credit(1), 4, false, "01089", asList(CardSubType.SENTRY), MatrixAnalyser::new);

	protected MatrixAnalyser(int id, MetaCard meta) {
		super(id, meta);
		// TODO Auto-generated constructor stub
	}

}

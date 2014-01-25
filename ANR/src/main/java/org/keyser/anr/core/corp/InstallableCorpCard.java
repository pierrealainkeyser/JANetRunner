package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.Installable;

public class InstallableCorpCard extends CorpCard implements Installable {

	public InstallableCorpCard(Influence influence, Cost cost, CardSubType... subtypes) {
		super(influence, cost, subtypes);
	}

}

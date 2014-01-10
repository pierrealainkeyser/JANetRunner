package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CorpArchivesServer extends CorpCentralServer {

	public CorpArchivesServer(Corp corpo) {
		super(corpo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorp().getDiscard();
	}

	@Override
	public CardAccessGroup getAccessedCards(CorpAccessSettings setting) {
		CardAccessGroup grp = super.getAccessedCards(setting);

		List<CorpCard> st = getCards();
		if (!st.isEmpty()) {
			List<CorpCard> lasts = new ArrayList<>(st);
			Collections.reverse(lasts);
			lasts.forEach(c -> grp.addSequential(c));
		}

		return grp;
	}

}

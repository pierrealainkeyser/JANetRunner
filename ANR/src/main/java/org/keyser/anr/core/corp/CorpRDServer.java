package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keyser.anr.core.CardLocation;

public final class CorpRDServer extends CorpCentralServer {

	public CorpRDServer(Corp corpo) {
		super(corpo);
	}

	public void add(CorpCard card) {
		card.setLocation(CardLocation.RD);
		getCards().add(card);
	}

	@Override
	public CardAccessGroup getAccessedCards(CorpAccessSettings setting) {
		CardAccessGroup grp = super.getAccessedCards(setting);

		int accededs = setting.getAccededs();
		List<CorpCard> st = getCards();
		if (!st.isEmpty()) {
			int size = st.size();
			int nb = Math.min(size, accededs);
			int from = size - nb;
			List<CorpCard> lasts = new ArrayList<>(st.subList(from, size));
			Collections.reverse(lasts);
			lasts.forEach(c -> grp.addSequential(c));
		}

		return grp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorp().getStack();
	}
}

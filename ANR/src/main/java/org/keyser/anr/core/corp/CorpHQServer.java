package org.keyser.anr.core.corp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.keyser.anr.core.CardLocation;

public final class CorpHQServer extends CorpCentralServer {

	public CorpHQServer(Corp corpo) {
		super(corpo);
	}

	public void add(CorpCard card) {
		card.setLocation(CardLocation.HQ);
	}

	@Override
	public CardAccessGroup getAccessedCards(CorpAccessSettings setting) {
		CardAccessGroup grp = super.getAccessedCards(setting);

		int accededs = setting.getAccededs();
		List<CorpCard> st = getCards();
		if (!st.isEmpty()) {
			// on accede aleatoirement à un certains nombre de carte
			int nb = Math.min(st.size(), accededs);
			LinkedList<CorpCard> lasts = new LinkedList<>(st);
			Collections.shuffle(lasts);

			while (lasts.size() > nb)
				lasts.removeLast();

			lasts.forEach(c -> grp.addSequential(c));
		}

		return grp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorp().getHand();
	}

}

package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.CardLocation;

public final class CorpRDServer extends CorpCentralServer {

	public CorpRDServer(Corp corpo) {
		super(corpo);
	}

	public void add(CorpCard card) {
		card.setLocation(CardLocation.RD);
		
		//TODO cela fait doublon
		getCards().add(card);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorp().getStack();
	}
}

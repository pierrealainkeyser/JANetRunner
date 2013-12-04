package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.CardLocation;


public final class CorpHQServer extends CorpCentralServer {

	public CorpHQServer(Corp corpo) {
		super(corpo);
	}
	
	public void add(CorpCard card) {
		card.setLocation(CardLocation.HQ);
		getCards().add(card);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorpo().getHand();
	}
	

	

}

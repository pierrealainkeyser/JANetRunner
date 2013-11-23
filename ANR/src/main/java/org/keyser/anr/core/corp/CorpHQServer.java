package org.keyser.anr.core.corp;

import java.util.List;


public final class CorpHQServer extends CorpCentralServer {

	public CorpHQServer(Corp corpo) {
		super(corpo);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorpo().getHand();
	}
	

	

}

package org.keyser.anr.core.corp;

import java.util.List;


public final class CorpRDServer extends CorpCentralServer {
	


	public CorpRDServer(Corp corpo) {
		super(corpo);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorpo().getStack();
	}
}

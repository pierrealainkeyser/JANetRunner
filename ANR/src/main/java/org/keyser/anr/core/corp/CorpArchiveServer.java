package org.keyser.anr.core.corp;

import java.util.List;


public final class CorpArchiveServer extends CorpCentralServer {

	public CorpArchiveServer(Corp corpo) {
		super(corpo);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpCard> getCards() {
		return (List<CorpCard>) getCorpo().getDiscard();
	}
	
	

}

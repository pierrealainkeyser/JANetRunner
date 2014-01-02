package org.keyser.anr.core.corp;

public abstract class CorpCentralServer extends CorpServer {

	public CorpCentralServer(Corp corpo) {
		super(corpo);
	}
	
	@Override
	public boolean isNotEmpty() {
		return true;
	}

}
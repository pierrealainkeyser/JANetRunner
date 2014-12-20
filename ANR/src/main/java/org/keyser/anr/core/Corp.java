package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.corp.CorpServer;

public class Corp extends AbstractId {

	private List<CorpServer> remotes = new ArrayList<>();

	public Corp(int id, MetaCard meta) {
		super(id, meta, PlayerType.CORP);
	}

	@Override
	public PlayerType getOwner() {
		return PlayerType.CORP;
	}

	public void eachServers(Consumer<CorpServer> c) {
		eachCentrals(c);
		eachRemotes(c);
	}

	public void eachRemotes(Consumer<CorpServer> c) {
		remotes.forEach(c);
	}

	public void eachCentrals(Consumer<CorpServer> c) {
		// TODO Auto-generated method stub
		
	}

}

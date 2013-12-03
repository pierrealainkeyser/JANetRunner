package org.keyser.anr.core.corp;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

public final class CorpRemoteServer extends CorpServer {

	private Asset asset;

	private Agenda agenda;

	private final int id;

	public CorpRemoteServer(Corp corpo, int id) {
		super(corpo);
		this.id = id;
	}

	public List<CorpCard> getCards() {
		if(asset!=null)
			return singletonList((CorpCard)asset);
		else if(agenda!=null)
			return singletonList((CorpCard)agenda);
		else
			return emptyList();
		
	}

	public void remove() {
		getCorpo().remove(this);
	}

	public int getId() {
		return id;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

}

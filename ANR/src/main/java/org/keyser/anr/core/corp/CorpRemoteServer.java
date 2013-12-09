package org.keyser.anr.core.corp;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.Card;

public final class CorpRemoteServer extends CorpServer {

	private Asset asset;

	private Agenda agenda;

	private final int id;

	public CorpRemoteServer(Corp corpo, int id) {
		super(corpo);
		this.id = id;
	}

	@Override
	public void forEach(Consumer<Card> c) {
		super.forEach(c);
		if (asset != null)
			c.accept(asset);
		if (agenda != null)
			c.accept(agenda);
	}

	public List<CorpCard> getCards() {
		if (asset != null)
			return singletonList((CorpCard) asset);
		else if (agenda != null)
			return singletonList((CorpCard) agenda);
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
		trashAgendaAndAsset();
		this.asset = asset;
	}

	private void trashAgendaAndAsset() {
		if (asset != null) {
			Asset old = asset;
			asset = null;
			old.trash();
		}
		if (agenda != null) {
			Agenda old = agenda;
			agenda = null;
			old.trash();

		}

	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		trashAgendaAndAsset();
		this.agenda = agenda;
	}

}

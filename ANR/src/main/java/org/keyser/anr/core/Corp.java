package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.CorpServerCentral;

public class Corp extends AbstractId {

	private List<CorpServer> remotes = new ArrayList<>();

	private CorpServerCentral archives;

	private CorpServerCentral rd;

	private CorpServerCentral hq;

	public Corp(int id, MetaCard meta) {
		super(id, meta, PlayerType.CORP);
	}

	public void init(Game game) {
		archives = new CorpServerCentral(game, nextServerId());
		rd = new CorpServerCentral(game, nextServerId());
		hq = new CorpServerCentral(game, nextServerId());
	}

	public int nextServerId() {
		int[] min = { 0 };

		eachServers(c -> {
			int id = c.getId();
			if (id < min[0])
				min[0] = id;
		});
		return min[0] - 1;

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
		if (archives != null)
			c.accept(archives);
		if (rd != null)
			c.accept(rd);
		if (hq != null)
			c.accept(hq);

	}

	public CorpServerCentral getArchives() {
		return archives;
	}

	public CorpServerCentral getRd() {
		return rd;
	}

	public CorpServerCentral getHq() {
		return hq;
	}

}

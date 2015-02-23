package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.CorpServerCentral;
import org.keyser.anr.core.corp.CorpServerDef;

public class Corp extends AbstractId {

	private List<CorpServer> remotes = new ArrayList<>();

	private CorpServerCentral archives;

	private CorpServerCentral rd;

	private CorpServerCentral hq;

	public Corp(int id, MetaCard meta) {
		super(id, meta, PlayerType.CORP);
	}

	/**
	 * Cr�ation des serveurs qui vont bien
	 */
	public void init() {
		archives = new CorpServerCentral(game, nextServerId());
		rd = new CorpServerCentral(game, nextServerId());
		hq = new CorpServerCentral(game, nextServerId());

		// on a toujours un remote vide
		createRemote();
	}

	/**
	 * Création de la définition de la corporation
	 * 
	 * @return
	 */
	public CorpDef createCorpDef() {
		CorpDef def = new CorpDef();
		updateIdDef(def);

		List<CorpServerDef> servers = new ArrayList<>();
		eachServers(c -> servers.add(c.createDef()));
		def.setServers(servers);
		return def;
	}

	public CorpServer createRemote() {
		CorpServer corpServer = new CorpServer(game, nextServerId());
		remotes.add(corpServer);
		// TODO event de création de server
		return corpServer;
	}

	/**
	 * Uniquement appelé par {@link CorpServer#delete()}
	 * 
	 * @param corpServer
	 */
	public void deleteServer(CorpServer corpServer) {
		remotes.remove(corpServer);
		// TODO event de suppression de sever

	}

	private int nextServerId() {
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

	public List<CorpServer> getRemotes() {
		return Collections.unmodifiableList(remotes);
	}

}

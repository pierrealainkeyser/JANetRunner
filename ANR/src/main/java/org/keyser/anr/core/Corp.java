package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.keyser.anr.core.CardCounterChangedEvent.Counter;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.CorpServerArchives;
import org.keyser.anr.core.corp.CorpServerDef;
import org.keyser.anr.core.corp.CorpServerHQ;
import org.keyser.anr.core.corp.CorpServerRD;

public class Corp extends AbstractId {

	private List<CorpServer> remotes = new ArrayList<>();

	private CorpServerArchives archives;

	private CorpServerRD rd;

	private CorpServerHQ hq;

	public Corp(int id, MetaCard meta) {
		super(id, meta, PlayerType.CORP, CardLocation::corpScore);
	}

	/**
	 * Chargement de la configuration
	 * 
	 * @param def
	 * @param creator
	 */
	public void load(CorpDef def, Function<AbstractTokenContainerId, AbstractCard> creator) {
		def.getServers().stream().forEach(csd -> getOrCreate(csd.getId()).load(csd, creator));
	}

	/**
	 * Création des serveurs qui vont bien
	 */
	public void init() {
		archives = new CorpServerArchives(game, nextServerId());
		archives.getStack().setListener(ac -> game.fire(new CardCounterChangedEvent(Counter.ARCHIVES, ac.size())));

		rd = new CorpServerRD(game, nextServerId());
		rd.getStack().setListener(ac -> game.fire(new CardCounterChangedEvent(Counter.RD, ac.size())));

		hq = new CorpServerHQ(game, nextServerId());
		hq.getStack().setListener(ac -> game.fire(new CardCounterChangedEvent(Counter.HQ, ac.size())));
	}

	@Override
	protected AbstractCardList cardsInHands() {
		AbstractCardList acl = new AbstractCardList();
		hq.getStack().stream().forEach(acl::add);
		return acl;
	}

	@Override
	public void draw(int i, Flow next) {

		AbstractCardContainer<AbstractCardCorp> rdStack = rd.getStack();
		AbstractCardContainer<AbstractCardCorp> hqStack = hq.getStack();
		int size = rdStack.size();
		if (i <= size) {
			List<AbstractCardCorp> cards = new ArrayList<>();
			for (int j = 0; j < i; j++)
				cards.add(rdStack.get(j));

			cards.stream().forEach(hqStack::add);
		} else {
			// corp à perdue
		}

		next.apply();

	}

	/**
	 * Permet de s'assurer qu'il a un serveur vide
	 */
	public void ensureEmptyServer() {
		if (!remotes.stream().filter(CorpServer::isEmpty).findFirst().isPresent()) {
			createRemote();
		}
	}

	/**
	 * Accède au serveur
	 * 
	 * @param serverId
	 * @return
	 */
	public CorpServer getOrCreate(int serverId) {
		if (serverId == -1)
			return archives;
		if (serverId == -2)
			return rd;
		if (serverId == -3)
			return hq;

		Optional<CorpServer> first = remotes.stream().filter(cs -> cs.getId() == serverId).findFirst();
		if (first.isPresent())
			return first.get();
		else
			return createRemote(serverId);

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

	private CorpServer createRemote(int id) {
		CorpServer corpServer = new CorpServer(game, id);
		remotes.add(corpServer);
		// TODO event de création de server
		return corpServer;
	}

	public CorpServer createRemote() {
		return createRemote(nextServerId());
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

	public CorpServerArchives getArchives() {
		return archives;
	}

	public CorpServerRD getRd() {
		return rd;
	}

	public CorpServerHQ getHq() {
		return hq;
	}

	public List<CorpServer> getRemotes() {
		return Collections.unmodifiableList(remotes);
	}
}

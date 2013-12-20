package org.keyser.anr.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Permet d'indexer les {@link GameAccess}
 * 
 * @author PAF
 * 
 */
public class GameRepository {

	private ConcurrentMap<String, GameAccess> gateways = new ConcurrentHashMap<>();

	public GameAccess get(String key) {
		return gateways.get(key);
	}

	public void add(GameAccessWrapper g) {
		gateways.put(g.getCorp().getId(), g.getCorp());
		gateways.put(g.getRunner().getId(), g.getRunner());
	}

}

package org.keyser.anr.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Permet d'indexer les {@link EndpointAccess}
 * 
 * @author PAF
 * 
 */
public class GameRepository {

	private ConcurrentMap<String, EndpointAccess> gateways = new ConcurrentHashMap<>();

	public EndpointAccess get(String key) {
		return gateways.get(key);
	}

	public void register(Endpoint e) {
		for (SuscriberKey allowed : e.getAlloweds())
			gateways.put(allowed.getKey(), new EndpointAccess(allowed, e));
	}
	
	public void unregister(Endpoint e){
		for (SuscriberKey allowed : e.getAlloweds())
			gateways.remove(allowed.getKey());
	}

}

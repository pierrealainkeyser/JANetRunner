package org.keyser.anr.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.keyser.anr.core.MetaGame;

/**
 * La passerelle entre un {@link ConnectedGameEndpoint} qui permet d'enregistrer des {@link GameOutput}
 * @author PAF
 *
 */
public class GameGateway implements GameOutput, GameEndpoint {

	private final ConnectedGameEndpoint endpoint;

	private final Map<GameOutput, Boolean> outputs = new ConcurrentHashMap<>();

	public GameGateway(ConnectedGameEndpoint endpoint) {
		this.endpoint = endpoint;
		endpoint.setBroadcastOutput(this);
	}

	@Override
	public void accept(GameOutput output, Object incomming) {
		endpoint.accept(output, incomming);
	}

	@Override
	public MetaGame getMetaGame() {
		return endpoint.getMetaGame();
	}

	public void register(GameOutput output) {
		outputs.put(output, true);

	}

	public void remove(GameOutput ouput) {
		outputs.remove(ouput);

	}

	@Override
	public void send(String type, Object content) {
		outputs.keySet().forEach(go -> go.send(type, content));

	}

}
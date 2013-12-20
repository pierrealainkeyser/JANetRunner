package org.keyser.anr.web;

/**
 * L'access à une passerele {@link GameGateway} pour une faction donnée
 * 
 * @author PAF
 * 
 */
public class GameAccess {

	private final String id;

	private final String faction;

	private final GameGateway gateway;

	public GameAccess(String id, String faction, GameGateway gateway) {
		this.id = id;
		this.faction = faction;
		this.gateway = gateway;
	}

	public String getId() {
		return id;
	}

	public String getFaction() {
		return faction;
	}

	public GameGateway getGateway() {
		return gateway;
	}

	@Override
	public String toString() {
		return faction + "=" + id;
	}
}

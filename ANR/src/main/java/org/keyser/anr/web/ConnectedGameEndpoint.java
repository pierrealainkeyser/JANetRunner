package org.keyser.anr.web;

/**
 * Un point connecté qui sait renvoyer dans un {@link GameOutput}
 * 
 * @author PAF
 * 
 */
public interface ConnectedGameEndpoint extends GameEndpoint {
	/**
	 * Enregistre le point de diffusion à tous les joueurs
	 * 
	 * @param broadcast
	 */
	public void setBroadcastOutput(GameOutput broadcast);
}

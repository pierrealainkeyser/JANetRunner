package org.keyser.anr.web;

import org.keyser.anr.core.MetaGame;

/**
 * Permet de recevoir des messages
 * 
 * @author PAF
 * 
 */
public interface GameEndpoint {

	public static final String READY = "ready";

	public static final String RESPONSE = "response";

	/**
	 * Transmission du message
	 * 
	 * @param output
	 *            l'emetteur du message
	 * @param incomming
	 *            le message
	 */
	public void accept(GameOutput output, Object incomming);

	/**
	 * Renvoi la description du message
	 * 
	 * @return
	 */
	public MetaGame getMetaGame();

}

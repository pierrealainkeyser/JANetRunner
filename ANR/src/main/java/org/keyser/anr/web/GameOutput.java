package org.keyser.anr.web;

public interface GameOutput {

	/**
	 * Permet d'envoyer un message
	 * 
	 * @param type
	 * @param content
	 */
	public  void send(String type, Object content);

}
package org.keyser.anr.web;


public interface GameFactory {

	/**
	 * Créaion de l'accès
	 * 
	 * @param def
	 * @param faction
	 * @param gateway
	 * @return
	 */
	public abstract GameAccess createAccess(GameDef def, String faction, GameGateway gateway);

	/**
	 * Création du point
	 * 
	 * @param def
	 * @return
	 */
	public abstract ConnectedGameEndpoint create(GameDef def);

}
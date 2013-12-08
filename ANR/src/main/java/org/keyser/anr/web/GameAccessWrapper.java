package org.keyser.anr.web;

import java.util.function.Function;

/**
 * Permet de regrouper les accès pour tous les joueurs
 * @author PAF
 *
 */
public class GameAccessWrapper {

	private GameAccess corp;

	private final GameDef def;

	private GameAccess runner;

	private GameAccess visitor;

	public GameAccessWrapper(GameDef def) {
		this.def = def;
	}

	/**
	 * Création des access
	 * 
	 * @param creator
	 */
	public void create(Function<GameDef, GameGateway> creator) {

		GameGateway gg = creator.apply(def);
		corp = new GameAccess(def.getKey() + "-corp", "corp", gg);
		runner = new GameAccess(def.getKey() + "-runner", "runner", gg);
		visitor = new GameAccess(def.getKey(), "none", gg);
	}

	public GameAccess getCorp() {
		return corp;
	}

	public GameDef getDef() {
		return def;
	}

	public GameAccess getRunner() {
		return runner;
	}

	public GameAccess getVisitor() {
		return visitor;
	}

}

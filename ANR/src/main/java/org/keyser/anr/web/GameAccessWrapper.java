package org.keyser.anr.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Permet de regrouper les accès pour tous les joueurs
 * 
 * @author PAF
 * 
 */
public class GameAccessWrapper {

	private final Logger logger = LoggerFactory.getLogger(GameAccessWrapper.class);

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
	public void create(GameFactory gf) {

		GameAsDTOGateway gg = gf.create(def);

		// TODO utilise une autre maniere de procéder genre création d'un UID
		corp = gf.createAccess(def, "corp", gg);
		runner = gf.createAccess(def, "runner", gg);
		visitor = gf.createAccess(def, "visitor", gg);

		logger.debug("Game created {} with access : {} {} {}", def.getKey(), corp, runner, visitor);
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

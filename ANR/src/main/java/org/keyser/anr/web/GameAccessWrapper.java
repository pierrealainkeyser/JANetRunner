package org.keyser.anr.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Permet de regrouper les accès pour tous les joueurs. Utilise un
 * {@link GameFactory} pour créer les {@link GameAccess}
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

		// TODO passer un flow en parametre
		ConnectedGameEndpoint cge = gf.create(def);

		GameGateway gw = new GameGateway(cge);
		corp = gf.createAccess(def, "corp", gw);
		runner = gf.createAccess(def, "runner", gw);
		visitor = gf.createAccess(def, "visitor", gw);

		logger.debug("Game created {} with access : {} {} {}", def.getKey(), corp, runner, visitor);
	}

	public GameAccess getCorp() {
		return corp;
	}

	public GameAccess getRunner() {
		return runner;
	}

	public GameAccess getVisitor() {
		return visitor;
	}

}

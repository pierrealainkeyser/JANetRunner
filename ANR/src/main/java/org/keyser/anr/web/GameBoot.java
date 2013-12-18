package org.keyser.anr.web;

import org.springframework.beans.factory.InitializingBean;

/**
 * Juste pour une démo
 * 
 * @author PAF
 * 
 */
public class GameBoot implements InitializingBean {

	private GameRepository allGames;

	private GameFactory factory;

	private GameDef basic;

	@Override
	public void afterPropertiesSet() throws Exception {

		GameAccessWrapper gw = new GameAccessWrapper(basic);
		gw.create(factory);
		allGames.add(gw);

	}

	public void setAllGames(GameRepository allGames) {
		this.allGames = allGames;
	}

	public void setFactory(GameFactory factory) {
		this.factory = factory;
	}

	public void setBasic(GameDef basic) {
		this.basic = basic;
	}

}

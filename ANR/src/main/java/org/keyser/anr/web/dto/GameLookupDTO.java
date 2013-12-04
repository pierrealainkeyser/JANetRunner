package org.keyser.anr.web.dto;

public class GameLookupDTO {
	private String game;

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	@Override
	public String toString() {
		return "GameLookup [game=" + game + "]";
	}

}
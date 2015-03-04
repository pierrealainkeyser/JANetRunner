package org.keyser.anr.web.dto;

import java.util.List;

import org.keyser.anr.core.Clicks;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.UserActionContext;

public class GameDto {

	private List<ServerDto> servers;

	private List<CardDto> cards;

	private Clicks clicks;

	private UserActionContext primary;

	private PlayerType local;

	private FactionDto factions;

	private ScoreDto score;

	private TurnDTO turn;

	private List<String> chats;

	public List<CardDto> getCards() {
		return cards;
	}

	public Clicks getClicks() {
		return clicks;
	}

	public FactionDto getFactions() {
		return factions;
	}

	public PlayerType getLocal() {
		return local;
	}

	public UserActionContext getPrimary() {
		return primary;
	}

	public ScoreDto getScore() {
		return score;
	}

	public List<ServerDto> getServers() {
		return servers;
	}

	public void setCards(List<CardDto> cards) {
		this.cards = cards;
	}

	public void setClicks(Clicks clicks) {
		this.clicks = clicks;
	}

	public void setFactions(Faction corp, Faction runner) {
		this.factions = new FactionDto(corp, runner);
	}

	public void setLocal(PlayerType faction) {
		this.local = faction;
	}

	public void setPrimary(UserActionContext context) {
		this.primary = context;
	}

	public void setScore(int corp, int runner) {
		this.score = new ScoreDto(corp, runner);
	}

	public void setServers(List<ServerDto> servers) {
		this.servers = servers;
	}

	public TurnDTO getTurn() {
		return turn;
	}

	public void setTurn(TurnDTO turn) {
		this.turn = turn;
	}

	public List<String> getChats() {
		return chats;
	}

	public void setChats(List<String> chats) {
		this.chats = chats;
	}

}

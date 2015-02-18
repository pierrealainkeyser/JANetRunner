package org.keyser.anr.web.dto;

import java.util.List;

import org.keyser.anr.core.Clicks;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.UserActionContext;

public class GameDto {

	private List<ServerDto> servers;

	private List<CardDto> cards;
	
	private Clicks clicks;

	private UserActionContext primary;

	public List<CardDto> getCards() {
		return cards;
	}

	public Clicks getClicks() {
		return clicks;
	}

	public UserActionContext getPrimary() {
		return primary;
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



	public void setPrimary(UserActionContext context) {
		this.primary = context;
	}

	public void setServers(List<ServerDto> servers) {
		this.servers = servers;
	}

}

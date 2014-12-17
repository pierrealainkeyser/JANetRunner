package org.keyser.anr.web.dto;

import java.util.List;

public class GameDto {

	private List<ServerDto> servers;
	
	private List<CardDto> cards;

	public List<ServerDto> getServers() {
		return servers;
	}

	public void setServers(List<ServerDto> servers) {
		this.servers = servers;
	}

	public List<CardDto> getCards() {
		return cards;
	}

	public void setCards(List<CardDto> cards) {
		this.cards = cards;
	}

}

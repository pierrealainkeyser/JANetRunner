package org.keyser.anr.web.dto;

import java.util.List;

import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionContext;

public class GameDto {

	private List<ServerDto> servers;

	private List<CardDto> cards;

	private PlayerType active;

	private Integer actions;

	private UserActionContext context;

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

	public PlayerType getActive() {
		return active;
	}

	public void setActive(PlayerType active) {
		this.active = active;
	}

	public Integer getActions() {
		return actions;
	}

	public void setActions(Integer actions) {
		this.actions = actions;
	}

	public UserActionContext getContext() {
		return context;
	}

	public void setContext(UserActionContext context) {
		this.context = context;
	}

}

package org.keyser.anr.web.dto;

import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.TurnPhase;

public class TurnDTO {

	private final PlayerType player;

	private final String phase;

	public TurnDTO(PlayerType player, TurnPhase phase) {
		this.player = player;
		this.phase = phase.toString();
	}

	public PlayerType getPlayer() {
		return player;
	}

	public String getPhase() {
		return phase;
	}
}

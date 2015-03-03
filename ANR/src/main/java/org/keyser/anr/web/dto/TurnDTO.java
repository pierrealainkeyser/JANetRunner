package org.keyser.anr.web.dto;

import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.TurnPhase;

public class TurnDTO {

	private final PlayerType player;

	private final TurnPhase phase;

	public TurnDTO(PlayerType player, TurnPhase phase) {
		this.player = player;
		this.phase = phase;
	}

	public PlayerType getPlayer() {
		return player;
	}

	public TurnPhase getPhase() {
		return phase;
	}
}

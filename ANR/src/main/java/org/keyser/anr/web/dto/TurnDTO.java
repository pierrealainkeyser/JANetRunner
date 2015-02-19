package org.keyser.anr.web.dto;

import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.Turn.Phase;

public class TurnDTO {

	private final PlayerType player;

	private final Phase phase;

	public TurnDTO(PlayerType player, Phase phase) {
		this.player = player;
		this.phase = phase;
	}

	public PlayerType getPlayer() {
		return player;
	}

	public Phase getPhase() {
		return phase;
	}
}

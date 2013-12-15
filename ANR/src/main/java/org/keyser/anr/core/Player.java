package org.keyser.anr.core;

public enum Player {
	CORP, RUNNER;

	public NotificationEvent getScoreEvent() {

		if (this == CORP)
			return NotificationEvent.CORP_SCORE_AGENDA;
		return NotificationEvent.RUNNER_SCORE_AGENDA;
	}
}

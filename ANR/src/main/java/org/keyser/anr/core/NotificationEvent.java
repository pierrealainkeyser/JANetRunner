package org.keyser.anr.core;

public enum NotificationEvent {

	// events corps
	CORP_DRAW, //
	CORP_CLICKED_FOR_CREDIT, //
	CORP_CLICKED_FOR_DRAW, //
	CORP_CLICKED_FOR_PURGE, //
	CORP_PLAYED_AN_OPERATION, //
	CORP_ADVANCE_CARD, //
	CORP_INSTALLED_AN_ICE, //
	CORP_INSTALLED, //
	CORP_REZZ_CARD, //
	CORP_SCORE_AGENDA, //

	RUNNER_DRAW, //
	RUNNER_CLICKED_FOR_CREDIT, //
	RUNNER_CLICKED_FOR_DRAW, //
	RUNNER_PLAYED_AN_EVENT, //
	RUNNER_SCORE_AGENDA, //
	RUNNER_MEMORY_CHANGED, //
	RUNNER_TAG_CHANGED, //
	RUNNER_LINK_CHANGED, //
	RUNNER_INSTALLED, //

	// events du run
	START_OF_RUN, //
	APPROCHING_ICE, //
	APPROCHING_SERVER, //
	SORT_ON_ACCESS, //
	TRASH_CARD, //
	STEAL_AGENDA, //
	SHOW_ACCESSED_CARD, //
	END_OF_RUN, //
	SPECIAL_RUN, // un run spécial est lancé

	// events des cartes et du wallet
	WALLET_CHANGED, //
	CARD_LOC_CHANGED, //
	CARD_REZZ_CHANGED, //
	CARD_ADVANCED, //

	// questions
	WHICH_ACTION, //
	WHICH_ICE_TO_REZZ, //
	WHICH_ICEBREAKER, //
	WHICH_ABILITY, //
	DISCARD_CARD, //
	WANT_TO_JACKOFF, //

	// gestion du flow
	NEXT_STEP, //
	GAME_ENDED, //

	// sur les cartes
	CARD_POWER_COUNTER, //
	CARD_CREDITS;//

	public Notification apply() {
		return new Notification(this);
	}

}

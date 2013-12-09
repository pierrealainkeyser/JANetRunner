package org.keyser.anr.core;

public enum NotificationEvent {

	// events corps
	CORP_DRAW, //
	CORP_CLICKED_FOR_CREDIT, //
	CORP_CLICKED_FOR_DRAW, //
	CORP_CLICKED_FOR_PURGE, //
	CORP_PLAYED_AN_OPERATION, //

	// events du run
	START_OF_RUN, //
	END_OF_RUN, //

	// events des cartes et du wallet
	WALLET_CHANGED, //
	CARD_LOC_CHANGED, //
	CARD_REZZ_CHANGED, //

	// questions
	WHICH_ABILITY, //
	DISCARD_CARD, //
	WANT_TO_JACKOFF, //

	// gestion du flow
	NEXT_STEP, //
	GAME_ENDED;//

	public Notification apply() {
		return new Notification(this);
	}

}
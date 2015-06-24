package org.keyser.anr.core;

/**
 * Permet de confirmer la sélection
 * @author PAF
 *
 */
public class UserActionConfirmSelection extends UserActionArgs<AbstractCardList> {

	public UserActionConfirmSelection(AbstractId user, AbstractCard source) {
		super(user, source, null, "Done", AbstractCardList.class);
	}

}

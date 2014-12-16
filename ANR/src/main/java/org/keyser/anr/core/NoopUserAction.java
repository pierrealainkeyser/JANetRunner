package org.keyser.anr.core;

public class NoopUserAction extends UserAction {

	public NoopUserAction(AbstractId user, AbstractCard source, String description) {
		super(user, source, null, description);
	}

}

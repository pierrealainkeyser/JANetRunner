package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

public class FreeUserAction extends UserAction {

	public FreeUserAction(AbstractId user, AbstractCard source, String description) {
		super(user, source, null, description);
	}
	
	public FreeUserAction(AbstractId user, CorpServer source, String description) {
		super(user, source, null, description);
	}

}

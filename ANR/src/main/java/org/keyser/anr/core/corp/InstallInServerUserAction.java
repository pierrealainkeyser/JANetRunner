package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardList;
import org.keyser.anr.core.AbstractId;
import org.keyser.anr.core.UserActionWithArgs;

public class InstallInServerUserAction extends UserActionWithArgs<AbstractCardList> implements ActionOnServer {

	private final AbstractCardList cards;

	private final CorpServer server;

	public InstallInServerUserAction(AbstractId to, String description, CorpServer server, AbstractCardList cards) {
		super(to, null, null, description, AbstractCardList.class);
		this.cards = cards;
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see org.keyser.anr.core.corp.ActionOnServer#getServer()
	 */
	@Override
	public CorpServer getServer() {
		return server;
	}

	public AbstractCardList getCards() {
		return cards;
	}

}

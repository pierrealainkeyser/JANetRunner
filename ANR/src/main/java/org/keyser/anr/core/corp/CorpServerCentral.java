package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardContainer;
import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Game;

public class CorpServerCentral extends CorpServer {

	protected final AbstractCardContainer<AbstractCardCorp> stack = new AbstractCardContainer<>(this::stackLocation);

	private CardLocation stackLocation(Integer i) {
		return CardLocation.stack(id, i);
	}
	
	public CorpServerCentral(Game game, int id) {
		super(game, id);
	}

	public AbstractCardContainer<AbstractCardCorp> add(AbstractCardCorp a) {
		return stack.add(a);
	}

}
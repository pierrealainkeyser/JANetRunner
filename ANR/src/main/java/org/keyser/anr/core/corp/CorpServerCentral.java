package org.keyser.anr.core.corp;

import static org.keyser.anr.core.AbstractCard.createDefList;

import java.util.function.Function;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardContainer;
import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AbstractTokenContainerId;
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

	@Override
	public void load(CorpServerDef def, Function<AbstractTokenContainerId, AbstractCard> creator) {
		super.load(def, creator);
		game.getCorp().registerCard(def.getStack(), a -> stack.add((AbstractCardCorp) a), creator);
	}

	@Override
	public CorpServerDef createDef() {
		CorpServerDef def = super.createDef();
		def.setStack(createDefList(stack));
		return def;
	}

	public AbstractCardContainer<AbstractCardCorp> getStack() {
		return stack;
	}

}
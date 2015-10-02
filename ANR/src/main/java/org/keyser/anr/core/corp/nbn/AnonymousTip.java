package org.keyser.anr.core.corp.nbn;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Operation;

public class AnonymousTip extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Anonymous Tip", Faction.NBN.infl(1), Cost.credit(0), false, "01083", emptyList(), AnonymousTip::new);

	protected AnonymousTip(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void invoke(UserAction ua, Flow next) {
		Corp corp = getCorp();
		game.chat("{0} plays {1} and draws 3 cards", corp, this);
		corp.draw(3, () -> trash(TrashCause.PLAY, next));
	}
}

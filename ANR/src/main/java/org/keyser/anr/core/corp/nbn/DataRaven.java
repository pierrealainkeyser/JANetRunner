package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Run.IceIsEncounterEvent;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.runner.AddTagsEvent;

@CardDef(name = "Data Raven", oid = "01088")
public class DataRaven extends Ice {

	public DataRaven() {
		super(NBN.infl(2), credit(4), 4, CardSubType.SENTRY);

		add(match(IceIsEncounterEvent.class).pred(this::equals).name("DataRaven").async(this::applyEffect));
	}

	private void applyEffect(IceIsEncounterEvent e, Flow next) {
		Game g = getGame();
		Question q = g.ask(Player.RUNNER, NotificationEvent.CUSTOM_QUESTION);
		q.m("Take a tag or jackoff ?");
		q.ask("Take a tag").to(() -> {
			AddTagsEvent evt = new AddTagsEvent(1);
			evt.fire(g, next);
		});
		q.ask("Jack off").to(() -> {
			e.getRun().endedByRoutine();
			next.apply();
		});
		q.fire();
	}

}

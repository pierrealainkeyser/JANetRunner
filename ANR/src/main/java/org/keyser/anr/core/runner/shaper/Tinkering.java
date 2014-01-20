package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Tinkering", oid = "01037")
public class Tinkering extends EventCard {

	public Tinkering() {
		super(Faction.SHAPER.infl(4), Cost.credit(0));
	}

	@Override
	public boolean isEnabled() {
		return getGame().getCorp().hasIce();
	}

	@Override
	public void apply(Flow next) {

		Game game = getGame();
		Corp c = game.getCorp();

		Question q = game.ask(Player.RUNNER, NotificationEvent.TARGET_ICE);
		c.forEachIce((srv, ice) -> {
			q.ask("Add ice subtype", ice).to(() -> addSubtype(ice, next));
		});
		q.fire();
	}

	/**
	 * Pose la question du sous-type
	 * 
	 * @param ice
	 * @param next
	 */
	private void addSubtype(Ice ice, Flow next) {

		Game game = getGame();
		Question q = game.ask(Player.RUNNER, NotificationEvent.CLOSED_QUESTION);
		q.m("Select a subtype to add");
		q.ask("Add code gate").setContent("Code gate").to(() -> commitSubtype(ice, next, CardSubType.CODEGATE));
		q.ask("Add barrier").setContent("Barrier").to(() -> commitSubtype(ice, next, CardSubType.BARRIER));
		q.ask("Add sentry").setContent("Sentry").to(() -> commitSubtype(ice, next, CardSubType.SENTRY));
		q.fire();
	}

	private void commitSubtype(Ice ice, Flow next, CardSubType type) {
		ice.addSubtype(type);
		next.apply();
	}

}

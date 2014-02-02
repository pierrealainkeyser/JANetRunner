package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardTrashedEvent;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.Runner;
import org.keyser.anr.core.runner.RunnerInstalledHardware;

@CardDef(name = "Rabbit Hole", oid = "01039")
public class RabbitHole extends Hardware {

	public RabbitHole() {
		super(Faction.SHAPER.infl(1), Cost.credit(2));

		add(EventMatcher.match(RunnerInstalledHardware.class).pred(this::equals).call(this::install));
		add(EventMatcher.match(CardTrashedEvent.class).pred(this::equals).call(this::uninstall));
	}

	private void install() {
		Game game = getGame();
		Runner runner = game.getRunner();
		runner.alterLink(1);

		// FIXME recherche et installation d'un autre RabbitHole
	}

	private void uninstall() {
		getGame().getRunner().alterLink(1);
	}

}

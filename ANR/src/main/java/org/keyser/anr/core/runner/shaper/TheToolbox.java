package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardTrashedEvent;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Game.RunnerStartOfTurnEvent;
import org.keyser.anr.core.WalletRecuringCredits;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.RunnerOld;
import org.keyser.anr.core.runner.RunnerInstalledCleanup;
import org.keyser.anr.core.runner.UseIceBreaker;

@CardDef(name = "The Toolbox", oid = "01041")
public class TheToolbox extends Hardware {

	private WalletRecuringCredits wrc = new WalletRecuringCredits(null, this, (v) -> v instanceof UseIceBreaker, 2, RunnerStartOfTurnEvent.class);

	public TheToolbox() {
		super(Faction.SHAPER.infl(2), Cost.credit(9));

		add(match(RunnerInstalledCleanup.class).pred(this::equals).call(this::install));
		add(match(CardTrashedEvent.class).pred(this::equals).call(this::uninstall));
	}

	private void install() {
		RunnerOld runner = getGame().getRunner();
		runner.alterMemory(2);
		runner.getWallet().add(wrc);
		runner.alterLink(-2);
	}

	private void uninstall() {
		RunnerOld runner = getGame().getRunner();
		runner.alterMemory(-2);
		runner.getWallet().remove(wrc);
		runner.alterLink(-2);
	}

}

package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;

import java.util.ArrayList;
import java.util.List;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostCredit;
import org.keyser.anr.core.CostDeterminationEvent;
import org.keyser.anr.core.DefaultInstallable;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.runner.EventCard;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.HardwareInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.Program;
import org.keyser.anr.core.runner.ProgramInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.RunnerOld;

@CardDef(name = "Modded", oid = "01035")
public class Modded extends EventCard {

	public Modded() {
		super(Faction.SHAPER.infl(2), Cost.credit(0));
	}

	@Override
	public boolean isEnabled() {
		return listAllAbilities().length > 0;
	}

	private AbstractAbility[] listAllAbilities() {

		Game g = getGame();

		// on modifie le cout
		DefaultInstallable di = new DefaultInstallable();
		di.add(match(ProgramInstallationCostDeterminationEvent.class).auto().sync(this::reduceCost));
		di.add(match(HardwareInstallationCostDeterminationEvent.class).auto().sync(this::reduceCost));
		di.bind(g);

		RunnerOld runner = g.getRunner();
		List<AbstractAbility> a = new ArrayList<>();
		runner.getHand().forEach(c -> {
			if (c instanceof Program)
				runner.addInstallProgramAbility(a, (Program) c);
			else if (c instanceof Hardware)
				runner.addInstallHardwareAbility(a, (Hardware) c);

		});

		di.unbind(g);

		return a.stream().filter(aa -> aa.isAffordable(runner.getWallet())).toArray(s -> new AbstractAbility[s]);
	}

	/**
	 * Réduction du cout de 3
	 * 
	 * @param cde
	 */
	private void reduceCost(CostDeterminationEvent cde) {
		Cost cost = cde.getEffective();
		int nb = cost.sumFor(CostCredit.class);
		int delta = Math.min(nb, 3);
		if (delta > 0) {
			cost.add(credit(-delta));
		}
	}

	@Override
	public void apply(Flow next) {

		Question q = ask(Player.RUNNER, NotificationEvent.CUSTOM_QUESTION);
		q.m("Applying Modded");
		Wallet wallet = getGame().getRunner().getWallet();

		// on enregistre l'action du modded
		for (AbstractAbility aa : listAllAbilities())
			aa.register(q, wallet, next);

		q.fire();

	}
}

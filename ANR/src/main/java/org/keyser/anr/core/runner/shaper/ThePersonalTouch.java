package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.keyser.anr.core.AbstractCardTrashed;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.HostedCard;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.SimpleFeedback;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.InstallHardwareAction;
import org.keyser.anr.core.runner.Program;

public class ThePersonalTouch extends Hardware {

	public final static MetaCard INSTANCE = new MetaCard("The Personal Touch", SHAPER.infl(2), Cost.credit(2), false, "01040", emptyList(), ThePersonalTouch::new);

	protected ThePersonalTouch(int id, MetaCard meta) {
		super(id, meta);

		match(AbstractCardTrashed.class, actc -> onTrashed(actc));
	}

	private void onTrashed(EventMatcherBuilder<AbstractCardTrashed> actc) {
		actc.test(AbstractCardTrashed.with(myself()));
		actc.run(() -> {
			HostedCard host = getHost();
			if (host != null) {
				IceBreaker parent = (IceBreaker) host.getHost();
				parent.alterBonus(-1);
			}
		});
	}

	@Override
	protected Predicate<CollectHabilities> customizePlayPredicate(Predicate<CollectHabilities> pred) {
		pred = pred.and(runner(r -> r.getPrograms().stream().anyMatch(IS_ICEBREAKER)));
		return pred;

	}

	@Override
	protected void install(Flow next) {

		Game g = getGame();
		List<Program> iceBreakers = getRunner().getPrograms().stream().filter(IS_ICEBREAKER).collect(Collectors.toList());
		CostForAction cost = new CostForAction(Cost.free(), new InstallHardwareAction(this));

		// TODO placer le contexte

		for (Program p : iceBreakers) {
			UserAction ua = new UserAction(getRunner(), p, cost, "Host on this");
			g.user(new SimpleFeedback<>(ua, this::installed), next);
		}
	}

	private void installed(UserAction ua, Flow next) {

		IceBreaker p = (IceBreaker) ua.getSource();
		setHost(p);
		p.alterBonus(1);

		doCleanUp(next);

	}
}

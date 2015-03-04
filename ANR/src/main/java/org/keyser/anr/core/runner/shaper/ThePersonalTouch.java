package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.HostType;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.SimpleFeedback;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.InstallHardwareAction;

public class ThePersonalTouch extends Hardware {

	public final static MetaCard INSTANCE = new MetaCard("The Personal Touch",
			SHAPER.infl(2), Cost.credit(2), false, "01040", emptyList(),
			ThePersonalTouch::new);

	private final static Predicate<AbstractCard> IS_AN_INSTALLED_ICEBREAKER = IS_ICEBREAKER
			.and(AbstractCard::isInstalled);

	protected ThePersonalTouch(int id, MetaCard meta) {
		super(id, meta);

		whileInstalled(null, this::uninstalled);
	}

	private void uninstalled(Flow next) {
		AbstractCard host = getHost();
		if (host != null) {
			IceBreaker parent = (IceBreaker) host;
			parent.setBonusStrength(parent.getBonusStrength() - 1);
		}
		next.apply();
	}

	@Override
	protected Predicate<CollectHabilities> customizePlayPredicate(
			Predicate<CollectHabilities> pred) {
		pred = pred.and(p -> cards().anyMatch(IS_AN_INSTALLED_ICEBREAKER));
		return pred;

	}

	@Override
	protected void install(Flow next) {

		Game g = getGame();
		List<AbstractCard> iceBreakers = cards().filter(
				IS_AN_INSTALLED_ICEBREAKER).collect(Collectors.toList());
		CostForAction cost = new CostForAction(Cost.free(),
				new InstallHardwareAction(this));

		// placer le contexte
		g.userContext(this, "Choose an host");
		for (AbstractCard p : iceBreakers) {
			UserAction ua = new UserAction(getRunner(), p, cost, "Host on this");
			g.user(new SimpleFeedback<>(ua, this::installed), next);
		}
	}

	private void installed(UserAction ua, Flow next) {

		IceBreaker p = (IceBreaker) ua.getSource();
		p.hostCard(this, HostType.CARD);
		p.setBonusStrength(p.getBonusStrength() + 1);
		cleanupInstall(next);

	}
}

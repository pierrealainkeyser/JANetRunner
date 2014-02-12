package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.EventMatcher.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardLocationHosted;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.RunnerInstalledHardware;

@CardDef(name = "The Personal Touch", oid = "01040")
public class ThePersonalTouch extends Hardware {

	public ThePersonalTouch() {
		super(Faction.SHAPER.infl(2), Cost.credit(2));

		add(match(RunnerInstalledHardware.class).pred(this::equals).call(() -> alterBreakerStrength(1)));
	}

	private void alterBreakerStrength(int delta) {
		IceBreaker ib = (IceBreaker) getHost();
		// quand on trash
		if (ib != null)
			ib.setBonusStrength(ib.getBonusStrength() + delta);

	}

	@Override
	public void doTrash() {
		// on supprime le bonus du breaker
		alterBreakerStrength(-1);
		super.doTrash();
	}

	@Override
	public Collection<CardLocation> possibleInstallPlaces() {

		List<CardLocation> locs = new ArrayList<>();
		getGame().getRunner().forEachProgramSpace(ps -> ps.forEach(p -> {
			if (p instanceof IceBreaker)
				locs.add(new CardLocationHosted(p));

		}));

		return locs;
	}
}

package org.keyser.anr.core.runner.shaper;


//@CardDef(name = "The Personal Touch", oid = "01040")
public class ThePersonalTouch //extends Hardware 
{

	public ThePersonalTouch() {
//		super(Faction.SHAPER.infl(2), Cost.credit(2));
//
//		registerPrevent(match(RunnerInstalledCleanup.class).pred(this::equals).call(() -> alterBreakerStrength(1)));
	}

//	private void alterBreakerStrength(int delta) {
//		IceBreaker ib = (IceBreaker) getHost();
//		// quand on trash
//		if (ib != null)
//			ib.setBonusStrength(ib.getBonusStrength() + delta);
//
//	}
//
//	@Override
//	public void doTrash() {
//		// on supprime le bonus du breaker
//		alterBreakerStrength(-1);
//		super.doTrash();
//	}
//
//	@Override
//	public Collection<CardLocation> possibleInstallPlaces() {
//
//		List<CardLocation> locs = new ArrayList<>();
//		getGame().getRunner().forEachProgramSpace(ps -> ps.forEach(p -> {
//			if (p instanceof IceBreaker)
//				locs.registerPrevent(new CardLocationHosted(p));
//
//		}));
//
//		return locs;
//	}
}

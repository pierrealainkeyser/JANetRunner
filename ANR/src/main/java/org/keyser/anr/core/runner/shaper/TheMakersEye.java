package org.keyser.anr.core.runner.shaper;


//@CardDef(name = "The Maker's Eye", oid = "01036")
public class TheMakersEye //extends EventCard 
{

	public TheMakersEye() {
//		super(Faction.SHAPER.infl(2), Cost.credit(2));
	}

//	private static class TheMakersEyeRun extends CoolEffect {
//
//		private TheMakersEyeRun(Game g) {
//			super(CleanTheRunEvent.class);
//			registerPrevent(match(RunIsSuccessfulEvent.class).auto().sync(this::acces2MoreCards));
//			bind(g);
//		}
//
//		/**
//		 * On accede 2 cartes de plus
//		 * 
//		 * @param evt
//		 */
//		private void acces2MoreCards(RunIsSuccessfulEvent evt) {
//			CorpAccessSettings settings = evt.getCorpAccess();
//			settings.setAccededs(2 + settings.getAccededs());
//		}
//	}
//
//	@Override
//	public void apply(Flow next) {
//		Game g = getGame();
//		new TheMakersEyeRun(g);
//		g.startRun(g.getCorp().getRd(), next).apply();
//
//	}
}

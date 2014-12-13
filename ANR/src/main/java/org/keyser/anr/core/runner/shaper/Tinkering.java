package org.keyser.anr.core.runner.shaper;


//@CardDef(name = "Tinkering", oid = "01037")
public class Tinkering 
//extends EventCard 
{

	// public Tinkering() {
	// super(Faction.SHAPER.infl(4), Cost.credit(0));
	// }
	//
	// @Override
	// public boolean isEnabled() {
	// return getGame().getCorp().hasIce();
	// }
	//
	// @Override
	// public void apply(Flow next) {
	//
	// Game game = getGame();
	// Corp c = game.getCorp();
	//
	// Question q = game.ask(Player.RUNNER, NotificationEvent.TARGET_ICE);
	// c.forEachIce((srv, ice) -> {
	// q.ask("Add all subtypes", ice).to(() -> addSubtype(ice, next));
	// });
	// q.fire();
	// }
	//
	// /**
	// * L'effet qui rajoute les types ou les retire à la fin du tour
	// *
	// * @author PAF
	// *
	// */
	// private static class TinkeringEffect extends CoolEffect {
	//
	// private final Ice ice;
	//
	// public TinkeringEffect(Ice ice) {
	// super(RunnerTurnEndedEvent.class);
	// this.ice = ice;
	//
	// alterType(ice::addSubtype);
	// }
	//
	// @Override
	// public void unbind(ConfigurableEventListener conf) {
	// alterType(ice::removeSubtype);
	// super.unbind(conf);
	//
	// }
	//
	// private void alterType(Consumer<CardSubType> alter) {
	// alter.accept(CardSubType.BARRIER);
	// alter.accept(CardSubType.CODEGATE);
	// alter.accept(CardSubType.SENTRY);
	// }
	//
	// }
	//
	// /**
	// * Pose la question du sous-type
	// *
	// * @param ice
	// * @param next
	// */
	// private void addSubtype(Ice ice, Flow next) {
	//
	// Game game = getGame();
	//
	// // on attache le
	// TinkeringEffect clean = new TinkeringEffect(ice);
	// clean.bind(game);
	//
	// next.apply();
	// }
}

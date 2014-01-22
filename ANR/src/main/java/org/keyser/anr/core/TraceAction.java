package org.keyser.anr.core;

import java.util.function.BiConsumer;

import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.runner.Runner;

/**
 * L'action de tracer
 * 
 * @author PAF
 * 
 */
public class TraceAction {
	private final int strength;

	private int corpBoost;

	private final String text;

	private int runnerValue;

	private final BiConsumer<TraceAction, Flow> consumer;

	/**
	 * Un DTO pour l'IHM
	 * 
	 * @author PAF
	 * 
	 */
	public static class BoostedTrace {
		private final int yours;

		private final Integer match;

		private final int max;

		public BoostedTrace(int BoostedTrace, Integer match, int max) {
			this.yours = BoostedTrace;
			this.match = match;
			this.max = max;
		}

		public int getYours() {
			return yours;
		}

		public int getMax() {
			return max;
		}

		public Integer getMatch() {
			return match;
		}
	}

	public TraceAction(String text, int strength, BiConsumer<TraceAction, Flow> consumer) {
		this.text = text;
		this.strength = strength;
		this.consumer = consumer;
	}

	private int affordable(Wallet wallet) {
		return wallet.timesAffordable(Cost.credit(1), this);
	}

	private int getRunnerMatch() {
		return corpBoost + strength;
	}

	/**
	 * Applique la trace
	 * 
	 * @param g
	 * @param next
	 */
	public void apply(Game g, Flow next) {

		Corp c = g.getCorp();

		int corpMax = affordable(c.getWallet());

		Question q = g.ask(Player.CORP, NotificationEvent.TRACE_QUESTION);
		q.m(text);
		q.ask("increase-trace").setContent(new BoostedTrace(strength, null, corpMax)).to(int.class, i -> increaseCorp(g, next, i));
		q.fire();
	}

	private void increaseCorp(Game g, Flow next, int boost) {
		Runner r = g.getRunner();
		int runnerMax = affordable(r.getWallet());
		corpBoost = boost;
		
		g.getCorp().getWallet().consume(Cost.credit(boost), this);

		Question q = g.ask(Player.RUNNER, NotificationEvent.TRACE_QUESTION);
		q.m(text);
		q.ask("increase-trace").setContent(new BoostedTrace(r.getLink(), getRunnerMatch(), runnerMax)).to(int.class, i -> increaseRunner(g, next, i));
		q.fire();
	}

	/**
	 * Renvoi vrai si success
	 * 
	 * @return
	 */
	public boolean isSucessful() {
		return runnerValue < getRunnerMatch();
	}

	private void increaseRunner(Game g, Flow next, int boost) {

		int link = g.getRunner().getLink();
		this.runnerValue = link + boost;
		
		g.getRunner().getWallet().consume(Cost.credit(boost), this);

		// on passe le resultat et on marque la trace comme réussi ensuite
		consumer.accept(this, () -> g.apply(new TraceResultEvent(this), next));

	}

}

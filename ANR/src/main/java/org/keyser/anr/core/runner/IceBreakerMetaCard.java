package org.keyser.anr.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.AbstractCardFactory;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Influence;

public class IceBreakerMetaCard extends ProgramMetaCard {

	public abstract static class AbstractUsage {
		private final Cost cost;

		protected AbstractUsage(Cost cost) {
			this.cost = cost;
		}

		public Cost getCost() {
			return cost;
		}
		
		public CostForAction getCostForAction(){
			return new CostForAction(getCost(), this);
		}
	}

	public static class BoostUsage extends AbstractUsage {
		private final int boost;

		protected BoostUsage(Cost cost, int boost) {
			super(cost);
			this.boost = boost;
		}

		public int getBoost() {
			return boost;
		}

	}

	public static class BreakSubUsage extends AbstractUsage {
		private final int broken;

		protected BreakSubUsage(Cost cost, int broken) {
			super(cost);
			this.broken = broken;
		}

		public int getBroken() {
			return broken;
		}
	}

	public class IceBreakerUsage {

		public IceBreakerUsage sub(Cost cost, int sub) {
			breaks.add(new BreakSubUsage(cost, sub));
			return this;
		}

		public IceBreakerUsage boost(Cost cost, int boost) {
			boosts.add(new BoostUsage(cost, boost));
			return this;
		}
	}

	private final List<BoostUsage> boosts = new ArrayList<>();

	private final List<BreakSubUsage> breaks = new ArrayList<>();

	private final int strength;

	public IceBreakerMetaCard(String name, Influence influence, Cost cost, boolean unique, String graphic, int memoryUnit, List<CardSubType> subTypes, int strength, AbstractCardFactory factory, Consumer<IceBreakerUsage> builder) {
		super(name, influence, cost, unique, graphic, memoryUnit, subTypes, factory);
		this.strength = strength;
		builder.accept(new IceBreakerUsage());
	}

	public List<BoostUsage> getBoosts() {
		return Collections.unmodifiableList(boosts);
	}

	public List<BreakSubUsage> getBreaks() {
		return Collections.unmodifiableList(breaks);
	}

	public int getStrength() {
		return strength;
	}

}

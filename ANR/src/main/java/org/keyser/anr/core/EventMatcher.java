package org.keyser.anr.core;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Permet de matcher un evenement.
 * 
 * @author PAF
 * 
 * @param <T>
 */
public final class EventMatcher<T extends Event> implements Comparable<EventMatcher<T>> {

	private static final int CORE_VALUE = 100;

	public static enum ApplyTo {
		RUNNER, CORPO, CURRENT_PLAYER
	}

	public static class Builder<T extends Event> {

		private final Class<T> matchedType;

		EventConsumer<T> consummer;

		Predicate<T> predicate;

		int order = 0;

		boolean interractive = true;

		ApplyTo applyTo = ApplyTo.CURRENT_PLAYER;

		String name;

		private Builder(Class<T> matchedType) {
			this.matchedType = matchedType;
		}

		public Builder<T> name(String name) {
			this.name = name;
			return this;
		}

		public Builder<T> async(EventConsumer<T> consummer) {
			this.consummer = consummer;
			return this;
		}

		public Builder<T> auto() {
			interractive = false;
			return this;
		}

		public EventMatcher<T> build() {
			return new EventMatcher<>(matchedType, predicate, consummer, order, applyTo, interractive, name);
		}

		public Builder<T> corpo() {
			applyTo = ApplyTo.CORPO;
			return this;
		}

		public Builder<T> runner() {
			applyTo = ApplyTo.RUNNER;
			return this;
		}

		public Builder<T> first() {
			order = -50;
			return this;
		}

		public Builder<T> last() {
			order = 50;
			return this;
		}

		public Builder<T> core() {
			order = CORE_VALUE;
			return auto();
		}

		public Builder<T> order(int order) {
			this.order = order;
			return this;
		}

		public Builder<T> pred(Predicate<T> predicate) {
			this.predicate = predicate;
			return this;
		}

		public Builder<T> call(Flow consummer) {
			return async((T t, Flow f) -> {
				consummer.apply();
				f.apply();
			});
		}

		public Builder<T> sync(Consumer<T> consummer) {
			return async((T t, Flow f) -> {
				consummer.accept(t);
				f.apply();
			});
		}

	}

	public static <T extends Event> Builder<T> match(Class<T> matchedType) {
		return new Builder<>(matchedType);
	}

	private final Class<T> matchedType;

	private final Predicate<T> predicate;

	private final EventConsumer<T> consummer;

	private final int order;

	private final ApplyTo applyTo;

	private final boolean interractive;

	private final String name;

	private Object bindKey;

	private EventMatcher(Class<T> matchedType, Predicate<T> predicate, EventConsumer<T> consummer, int order, ApplyTo applyTo, boolean interractive, String name) {
		this.matchedType = matchedType;
		this.predicate = predicate;
		this.consummer = consummer;
		this.order = order;
		this.applyTo = applyTo;
		this.interractive = interractive;
		this.name = name;
	}

	public void apply(Object event, Flow flow) {
		consummer.apply(cast(event), flow);
	}

	private T cast(Object event) {
		return getMatchedType().cast(event);
	}

	Object getBindKey() {
		return bindKey;
	}

	public Class<T> getMatchedType() {
		return matchedType;
	}

	public boolean isCore() {
		return order == CORE_VALUE;
	}

	public int getOrder() {
		return order;
	}

	void setBindKey(Object bindKey) {
		this.bindKey = bindKey;
	}

	public boolean test(Object o) {
		Class<T> mt = getMatchedType();
		return mt.equals(o.getClass()) && (predicate == null || predicate.test(cast(o)));
	}

	public ApplyTo getApplyTo() {
		return applyTo;
	}

	public boolean isInterractive() {
		return interractive;
	}

	@Override
	public int compareTo(EventMatcher<T> o) {
		return order - o.order;
	}

	@Override
	public String toString() {

		String sep = interractive ? "!" : "/";

		if (name != null)
			return sep + name + sep;
		else
			return sep + getClass().getName() + sep;
	}

	public String getName() {
		return name;
	}

}

package org.keyser.anr.core;

public interface FlowArg<T> {

	public void apply(T t);

	/**
	 * Transforme l'action en element asynchrone
	 * 
	 * @param t
	 * @return
	 */
	public default Flow as(T t) {
		return () -> apply(t);
	}

	/**
	 * Conversion en {@link EventConsumer}
	 * 
	 * @return
	 */
	public default EventConsumer<T> as() {
		return (t, next) -> {
			apply(t);
			next.apply();
		};
	}

}

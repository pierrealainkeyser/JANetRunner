package org.keyser.anr.core;

public interface Flow {

	public void apply();

	public default Flow then(Flow flow) {
		return () -> {
			apply();
			flow.apply();
		};
	}

	/**
	 * Conversion en {@link FlowArg}
	 * 
	 * @return
	 */
	public default <T> FlowArg<T> as() {
		return t -> apply();
	}

	/**
	 * Permet de créer un Flow qui appel {@link FlowArg#call} avec this.
	 * 
	 * @param call
	 * @return
	 */
	public default Flow wrap(FlowArg<Flow> call) {
		return () -> call.apply(this);
	}
}

package org.keyser.anr.core;

public class Response {

	private Class<Object> expected;

	private FlowArg<Object> next;

	private final String option;

	private final Question parent;

	private final int responseId;

	public Response(String option, Question parent, int responseId) {
		this.option = option;
		this.parent = parent;
		this.responseId = responseId;
	}

	/**
	 * Renvoi vrai si une réponse est impossible
	 * 
	 * @return
	 */
	public boolean isInvalid() {
		return next == null;
	}

	public void apply() {
		next.apply(null);
	}

	public void apply(Object o) {
		next.apply(o);
	}

	public boolean isExpectingArg() {
		return expected != null;
	}

	public Class<Object> getExpected() {
		return expected;
	}

	public String getOption() {
		return option;
	}

	public int getResponseId() {
		return responseId;
	}

	private void removeParent() {
		parent.remove();
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> Response to(Class<T> expected, FlowArg<T> c) {
		this.expected = (Class<Object>) expected;
		next = (FlowArg<Object>) (Object t) -> {
			removeParent();
			c.apply((T) t);
		};
		return this;
	}

	public Response to(Flow flow) {
		next = (f) -> {
			removeParent();
			flow.apply();
		};
		return this;
	}

	@Override
	public String toString() {
		return option;
	}

}

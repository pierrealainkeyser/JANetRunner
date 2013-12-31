package org.keyser.anr.core;

public class Response {

	private Class<Object> expected;

	private FlowArg<Object> next;

	private final String option;

	private final Question parent;

	private final int responseId;

	private final Card card;

	private Object content;

	private Cost cost;

	public Response(String option, Question parent, int responseId, Card card) {
		this.option = option;
		this.parent = parent;
		this.responseId = responseId;
		this.card = card;
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

	public Card getCard() {
		return card;
	}

	public Object getContent() {
		return content;
	}

	public Response setCost(Cost cost) {
		this.cost = cost;
		return this;
	}

	public Response setContent(Object content) {
		this.content = content;
		return this;
	}

	public Cost getCost() {
		return cost;
	}

}

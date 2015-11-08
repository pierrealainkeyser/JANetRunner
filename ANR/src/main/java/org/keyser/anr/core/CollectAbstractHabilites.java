package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectAbstractHabilites implements SequentialEvent {

	private final List<Feedback<?, ?>> feedbacks = new ArrayList<>();

	public CollectAbstractHabilites() {
		super();
	}

	public void add(Feedback<?, ?> uf) {
		feedbacks.add(uf);
	}

	public boolean hasFeedbacks() {
		return !feedbacks.isEmpty();
	}

	public Collection<Feedback<?, ?>> getFeedbacks() {
		return Collections.unmodifiableList(feedbacks);
	}

	@Override
	public String toString() {
		return "CollectAbstractHabilites [feedbacks=" + feedbacks + "]";
	}

}
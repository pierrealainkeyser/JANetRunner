package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class CollectHabilities implements SequentialEvent {

	public static final Predicate<CollectHabilities> CORP = ch -> ch.type == PlayerType.CORP;

	public static final Predicate<CollectHabilities> RUNNER = ch -> ch.type == PlayerType.RUNNER;

	private final PlayerType type;

	private final List<Feedback<?, ?>> feedbacks = new ArrayList<>();

	public CollectHabilities(PlayerType type) {
		this.type = type;
	}

	public void add(Feedback<?, ?> uf) {
		feedbacks.add(uf);
	}

	public Collection<Feedback<?, ?>> getFeedbacks() {
		return Collections.unmodifiableList(feedbacks);
	}

}

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

	private final boolean allowAction;

	private final List<Feedback<?, ?>> feedbacks = new ArrayList<>();

	public CollectHabilities(PlayerType type) {
		this(type, false);
	}

	public CollectHabilities(PlayerType type, boolean allowAction) {
		this.type = type;
		this.allowAction = allowAction;
	}

	public void add(Feedback<?, ?> uf) {
		feedbacks.add(uf);
	}
	
	public boolean hasFeedbacks(){
		return !feedbacks.isEmpty();
	}

	public Collection<Feedback<?, ?>> getFeedbacks() {
		return Collections.unmodifiableList(feedbacks);
	}

	@Override
	public String toString() {
		return "CollectHabilities [type=" + type + ", allowAction=" + allowAction + "]";
	}

	public boolean isAllowAction() {
		return allowAction;
	}

	public PlayerType getType() {
		return type;
	}

}

package org.keyser.anr.core;

import java.util.function.Predicate;

public class CollectHabilities extends CollectAbstractHabilites implements SequentialEvent {

	public static final Predicate<CollectHabilities> CORP = ch -> ch.type == PlayerType.CORP;

	public static final Predicate<CollectHabilities> RUNNER = ch -> ch.type == PlayerType.RUNNER;

	final PlayerType type;

	final boolean allowAction;

	public CollectHabilities(PlayerType type) {
		this(type, false);
	}

	public CollectHabilities(PlayerType type, boolean allowAction) {
		this.type = type;
		this.allowAction = allowAction;
	}

	@Override
	public String toString() {
		return "CollectHabilities [type=" + type + ", allowAction=" + allowAction + ", getFeedbacks()=" + getFeedbacks() + "]";
	}

	public boolean isAllowAction() {
		return allowAction;
	}

	public PlayerType getType() {
		return type;
	}

}

package org.keyser.anr.core.runner;

import java.util.stream.Stream;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.DefaultInstallable;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.Installable;

public abstract class InstallableRunnerCard extends RunnerCard implements Installable {

	/**
	 * FIXME  à  déplacer sur les cartes
	 */
	private final DefaultInstallable di = new DefaultInstallable();

	public InstallableRunnerCard(Influence influence, Cost cost, CardSubType... subtypes) {
		super(influence, cost, subtypes);
	}
	
	

	protected InstallableRunnerCard add(EventMatcher.Builder<?> em) {
		di.add(em);
		return this;
	}

	public Stream<EventMatcher<?>> getEventMatchers() {
		return di.getEventMatchers();
	}

}

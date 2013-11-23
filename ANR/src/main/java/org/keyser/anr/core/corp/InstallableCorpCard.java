package org.keyser.anr.core.corp;

import java.util.stream.Stream;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.DefaultInstallable;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.Installable;

public class InstallableCorpCard extends CorpCard implements Installable {

	private final DefaultInstallable di = new DefaultInstallable();

	public InstallableCorpCard(Influence influence, Cost cost) {
		super(influence, cost);
	}

	protected InstallableCorpCard add(EventMatcher.Builder<?> em) {
		di.add(em);
		return this;
	}

	public Stream<EventMatcher<?>> getEventMatchers() {
		return di.getEventMatchers();
	}

}

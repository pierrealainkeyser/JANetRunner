package org.keyser.anr.core.corp;

import java.util.stream.Stream;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.DefaultInstallable;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.Installable;

public class InstallableCorpCard extends CorpCard implements Installable {

	private final DefaultInstallable di = new DefaultInstallable();

	public InstallableCorpCard(Influence influence, Cost cost, CardSubType... subtypes) {
		super(influence, cost, subtypes);
	}

	protected InstallableCorpCard add(EventMatcher.Builder<?> em) {
		di.add(em);
		return this;
	}

	@Override
	public void setRezzed(boolean rezzed) {
		super.setRezzed(rezzed);
		di.bind(getGame());
	}

	@Override
	public void doTrash() {
		super.doTrash();
		// on se désintalle
		di.unbind(getGame());
	}

	public Stream<EventMatcher<?>> getEventMatchers() {
		return di.getEventMatchers();
	}

}

package org.keyser.anr.core;

/**
 * Un effet temporaire
 * 
 * @author PAF
 * 
 */
public abstract class CoolEffect<T> {

	private final EventMatchers matchers = new EventMatchers();

	public CoolEffect(AbstractCard source, Class<T> unbindOn) {
		EventMatcherBuilder<T> uninstallBuilder = EventMatcherBuilder.match(unbindOn, source);
		filterUninstall(uninstallBuilder);
		uninstallBuilder.run(this::uninstall);
		matchers.add(uninstallBuilder);
		accept(matchers);

		// gestion du bind
		source.getGame().bind(matchers);
	}

	protected void filterUninstall(EventMatcherBuilder<T> e) {

	}

	protected abstract void accept(EventMatchers matchers);

	public void uninstall() {
		matchers.uninstall();
	}
}

package org.keyser.anr.core;

/**
 * Un effet temporaire
 * 
 * @author PAF
 * 
 */
public abstract class CoolEffect<X extends AbstractCard, T> {

	protected final EventMatchers matchers = new EventMatchers();

	protected final X source;

	public CoolEffect(X source, Class<T> unbindOn) {
		EventMatcherBuilder<T> uninstallBuilder = EventMatcherBuilder.match(unbindOn, source);
		uninstallBuilder.apply(this::uninstall);
		matchers.add(uninstallBuilder);
		this.source = source;

		// gestion du bind
		source.getGame().bind(matchers);
	}

	protected Game getGame() {
		return getSource().getGame();
	}

	protected void uninstall(T t, Flow next) {
		matchers.uninstall();
		next.apply();
	}

	protected X getSource() {
		return source;
	}
}

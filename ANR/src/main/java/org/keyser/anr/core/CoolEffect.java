package org.keyser.anr.core;

/**
 * Un effet temporaire
 * 
 * @author PAF
 * 
 */
public abstract class CoolEffect {

	private final EventMatchers matchers = new EventMatchers();

	public CoolEffect(AbstractCard source, Class<?> unbindOn) {
		matchers.add(EventMatcherBuilder.match(unbindOn, source).run(this::uninstall));
		accept(matchers);
		source.getGame().bind(matchers);
	}

	protected abstract void accept(EventMatchers matchers);

	public void uninstall() {
		matchers.uninstall();
	}
}

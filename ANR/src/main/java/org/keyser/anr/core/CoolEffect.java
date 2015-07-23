package org.keyser.anr.core;

/**
 * Un effet temporaire
 * 
 * @author PAF
 * 
 */
public abstract class CoolEffect {

	protected final EventMatchers matchers = new EventMatchers();

	protected final AbstractCard source;

	public <T> CoolEffect(AbstractCard source, Class<T> unbindOn) {
		EventMatcherBuilder<T> uninstallBuilder = EventMatcherBuilder.match(unbindOn, source);
		uninstallBuilder.apply(this::uninstall);
		matchers.add(uninstallBuilder);
		this.source = source;

		// gestion du bind
		source.getGame().bind(matchers);
	}

	/**
	 * Permet de dumper l'état
	 * 
	 * @return
	 */
	public CoolEffectMemento createMemento() {
		return new CoolEffectMemento(this);
	}

	/**
	 * Permet de charger l'état
	 * 
	 * @param memento
	 */
	public void load(CoolEffectMemento memento) {

	}

	protected Game getGame() {
		return getSource().getGame();
	}

	protected <T> void uninstall(T t, Flow next) {
		matchers.uninstall();
		next.apply();
	}

	public AbstractCard getSource() {
		return source;
	}
}

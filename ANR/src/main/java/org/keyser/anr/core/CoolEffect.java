package org.keyser.anr.core;

/**
 * Un effet temporaire
 * 
 * @author PAF
 * 
 */
public abstract class CoolEffect extends ConfigurableInstallable {

	private ConfigurableEventListener parent;

	public CoolEffect(Class<? extends Event> unbindOn) {
		add(EventMatcher.match(unbindOn).core().call(() -> {
			unbind(parent);
		}));
	}

	@Override
	public void bind(ConfigurableEventListener conf) {
		this.parent = conf;
		super.bind(conf);
	}

}

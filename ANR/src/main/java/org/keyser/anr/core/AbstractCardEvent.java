package org.keyser.anr.core;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCardEvent {

	private AbstractCard primary;

	private String description;

	private AbstractCard secondary;

	protected AbstractCardEvent(AbstractCard primary, String description, AbstractCard secondary) {
		this.primary = primary;
		this.description = description;
		this.secondary = secondary;
	}

	public String getDescription() {
		return description;
	}

	public Game getGame() {
		return primary.getGame();
	}

	/**
	 * Format les evenements
	 * 
	 * @return
	 */
	public Map<String, Object> getInputs() {
		Map<String, Object> strs = new HashMap<>();
		if (primary != null)
			strs.put("primary", primary.getId());
		if (secondary != null)
			strs.put("secondary", secondary.getId());

		updateInputs(strs);
		return strs;
	}

	public AbstractCard getPrimary() {
		return primary;
	}

	public AbstractCard getSecondary() {
		return secondary;
	}

	protected void updateInputs(Map<String, Object> inputs) {
		// NO-OP
	}
}

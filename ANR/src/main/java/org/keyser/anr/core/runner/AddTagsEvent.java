package org.keyser.anr.core.runner;

import org.keyser.anr.core.Event;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;

/**
 * Permet de gerer le nombre de tags
 * 
 * @author PAF
 * 
 */
public class AddTagsEvent extends Event {
	private int tags;

	public AddTagsEvent(int tags) {
		this.tags = tags;
	}

	public int getTags() {
		return tags;
	}

	public void setTags(int tags) {
		this.tags = tags;
	}

	/**
	 * On envoi l'evenement
	 * 
	 * @param g
	 * @param next
	 */
	public void fire(Game g, Flow next) {
		g.apply(this, () -> {

			if (tags > 0) {
				Runner r = g.getRunner();
				r.setTags(r.getTags() + tags);
			}
			next.apply();

		});
	}

	@Override
	public String toString() {
		return "AddTagsEvent [tags=" + tags + "]";
	}
}
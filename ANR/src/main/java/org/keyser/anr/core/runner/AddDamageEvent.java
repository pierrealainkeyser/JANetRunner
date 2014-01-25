package org.keyser.anr.core.runner;

import org.keyser.anr.core.Event;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;

/**
 * Rajoute des dommages au runner
 * @author PAF
 *
 */
public class AddDamageEvent extends Event {

	public static enum DamageType {
		BRAIN, MEAT, NET;
	}

	private int damage;

	private DamageType type;

	public AddDamageEvent(int damage, DamageType type) {
		this.damage = damage;
		this.type = type;
	}

	/**
	 * On envoi l'evenement
	 * 
	 * @param g
	 * @param next
	 */
	public void fire(Game g, Flow next) {
		g.apply(this, () -> {

			if (damage > 0) {
				Runner r = g.getRunner();

				// rajoute les brains au besoin
				if (DamageType.BRAIN == getType())
					r.setBrainDamages(r.getBrainDamages() + damage);
				r.doDamage(damage, next);

			} else
				next.apply();

		});
	}

	public int getDamage() {
		return damage;
	}

	public DamageType getType() {
		return type;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public String toString() {
		return "AddDamageEvent [damage=" + damage + ", type=" + type + "]";
	}

}

package org.keyser.anr.core.corp.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaAbility;
import org.keyser.anr.core.runner.DoDamageEvent;
import org.keyser.anr.core.runner.DoDamageEvent.DamageType;
import org.keyser.anr.core.runner.RunnerPreventibleEffect;

@CardDef(name = "Private Security Force", oid = "01107")
public class PrivateSecurityForce extends Agenda {
	public static final String DO_1_MEAT_DAMAGE = "Do 1 meat damage";

	private class MeatDamageAbility extends AgendaAbility {


		protected MeatDamageAbility(Agenda card) {
			super(card, DO_1_MEAT_DAMAGE, Cost.action(1));
		}

		@Override
		public boolean isEnabled() {
			return super.isEnabled() && getGame().getRunner().isTagged();
		}

		@Override
		public void apply() {
			RunnerPreventibleEffect event = new DoDamageEvent(1, DamageType.MEAT);
			event.fire(getGame(), next);
		}

	}

	public PrivateSecurityForce() {
		super(Faction.CORP_NEUTRAL, 2, 4);

		addAction(new MeatDamageAbility(this));
	}
}

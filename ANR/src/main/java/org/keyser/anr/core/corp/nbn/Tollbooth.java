package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Run.IceIsEncounterEvent;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.routines.EndTheRun;

@CardDef(name = "Tollbooth", oid = "01090")
public class Tollbooth extends Ice {

	public Tollbooth() {
		super(NBN.infl(2), credit(8), 5, CardSubType.CODEGATE);
		addRoutine(new EndTheRun());

		register(match(IceIsEncounterEvent.class).pred(this::equals).name("Tollbooth").wrap(this::applyEffect));
	}

	private void applyEffect(IceIsEncounterEvent e, Flow next) {

		Wallet w = getGame().getRunner().getWallet();
		Cost c1 = Cost.credit(1);
		int nb = w.timesAffordable(c1, null, 3);

		// on consomme les sous
		if (nb > 0)
			w.consume(c1.times(nb));

		// on verifie
		if (nb < 3) {
			// on arrete le run
			e.getRun().endedByRoutine();
		}

		next.apply();
	}
}

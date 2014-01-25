package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;

public abstract class Agenda extends InstallableCorpCard {

	private final int score;

	private final int requirement;

	public Agenda(Faction f, int score, int requirement) {
		super(f.infl(0), Cost.free());
		this.score = score;
		this.requirement = requirement;
	}

	public int getScore() {
		return score;
	}

	public int getRequirement() {
		return requirement;
	}

	/**
	 * Renvoi vrai si on peut marquer l'agenda
	 * 
	 * @return
	 */
	public boolean isScorable() {

		Integer adv = getAdvancement();

		// il faut envoyer un evenement dans le moteur !!
		DetermineAgendaRequirement dar = getGame().apply(new DetermineAgendaRequirement(requirement));

		int req = dar.getRequirement();
		return adv != null && adv >= req;
	}

	/**
	 * Renvoi vrai si l'agenda est scoré
	 * 
	 * @return
	 */
	public boolean isScored() {
		return getLocation() == CardLocation.CORP_SCORE;
	}

	@Override
	public boolean isAdvanceable() {
		return true;
	}

	/**
	 * L'agenda est volé
	 * 
	 * @param next
	 */
	public void steal(Flow next) {
		setLocation(CardLocation.RUNNER_SCORE);

		setRezzed(true);

		// on supprime les avancements
		setAdvancement(null);

		getGame().apply(new RunnerStealAgenda(this), next);
	}

}

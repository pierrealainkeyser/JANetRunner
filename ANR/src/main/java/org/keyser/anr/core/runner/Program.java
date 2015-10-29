package org.keyser.anr.core.runner;

import java.util.List;
import java.util.Optional;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardRunner;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.EventConsumer;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserDragAction;

public abstract class Program extends AbstractCardRunner {
	protected Program(int id, ProgramMetaCard meta) {
		super(id, meta);
	}

	@Override
	protected ProgramMetaCard getMeta() {
		return (ProgramMetaCard) super.getMeta();
	}
	
	/**
	 * Calcul de l'utilisation m�moire d'un program
	 * @return
	 */
	public int computeMemoryUsage(){
		return getMeta().getMemoryUnit();
	}

	@Override
	public void playFeedback(CollectHabilities hab) {
		Runner r = getRunner();
		Game g = getGame();
		CollectPossibleProgramsArea cppa = new CollectPossibleProgramsArea(this).addArea(r);
		g.fire(cppa);
		List<ProgramsArea> possibles = cppa.getAreas();

		CostForAction cost = new CostForAction(getCostWithAction(), new InstallProgramAction(this));
		hab.add(new UserAction(r, this, cost, "Install").spendAndApply(next -> prepareInstall(possibles, next)));

		// gestion du drag pour l'installation
		UserDragAction<AbstractCard> drag = new UserDragAction<>(r, this, cost, AbstractCard.class);
		for (ProgramsArea possible : possibles) {

			Optional<AbstractCard> host = possible.getProgramsHost();
			if (host.isPresent()) {
				AbstractCard hostv = host.get();
				drag.add(hostv, hostv.getLocation());
			} else {
				drag.play(null);
			}
		}
		hab.add(drag.spendAndApplyArg((host, next) -> {
			if (host != null)
				installedOnArea((ProgramsArea) host, next);
			else
				installedOnArea(r, next);
		}));
	}

	/**
	 * demande ou installer la carte
	 * 
	 * @param next
	 */
	private void prepareInstall(List<ProgramsArea> possibles, Flow next) {

		Game g = getGame();

		if (possibles.size() == 1) {
			// installation automatique
			installedOnArea(possibles.get(0), next);
		} else {
			g.userContext(this, "Choose an area");
			Runner r = getRunner();
			for (ProgramsArea possible : possibles) {

				Optional<AbstractCard> host = possible.getProgramsHost();
				EventConsumer<UserAction> installedOn = (ua, n) -> installedOnArea(possible, n);
				if (host.isPresent()) {
					g.user(new UserAction(r, host.get(), null, "Install").apply(installedOn), next);
				} else {
					g.user(new UserAction(r, this, null, "Main memory").apply(installedOn), next);
				}
			}
		}

	}

	/**
	 * Permet de gerer l'installation
	 * 
	 * @param selected
	 * @param next
	 */
	private void installedOnArea(ProgramsArea selected, Flow next) {
		setRezzed(true);
		setInstalled(true);
		defaultInstallChat();
		selected.installProgram(this, next.wrap(this::cleanupInstall));
	}
}

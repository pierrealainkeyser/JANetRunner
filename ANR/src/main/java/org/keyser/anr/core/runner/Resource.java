package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardRunner;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.UserAction;

public abstract class Resource extends AbstractCardRunner {
	protected Resource(int id, MetaCard meta) {
		super(id, meta);
	}
	
	public void defaultPlayChat() {
		game.chat("{0} installs {1}", getRunner(), this);
	}
	

	@Override
	public void playFeedback(CollectHabilities hab) {
		UserAction playOperation = new UserAction(getRunner(), this, new CostForAction(getCostWithAction(), new InstallResourceAction(this)), "Install").enabledDrag();
		hab.add(playOperation.spendAndApply(this::install));
	}

	protected void install(Flow next) {
		Runner runner = getRunner();
		setRezzed(true);
		setInstalled(true);
		defaultPlayChat();
		runner.getResources().add(this);		
		cleanupInstall(next);
	}
}

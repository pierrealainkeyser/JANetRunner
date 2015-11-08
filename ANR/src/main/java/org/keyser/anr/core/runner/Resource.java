package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardRunner;
import org.keyser.anr.core.CollectAbstractHabilites;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.UserAction;

public abstract class Resource extends AbstractCardRunner {
	protected Resource(int id, MetaCard meta) {
		super(id, meta);
	}
	

	

	@Override
	public void playFeedback(CollectAbstractHabilites hab) {
		UserAction playOperation = new UserAction(getRunner(), this, new CostForAction(getCostWithAction(), new InstallResourceAction(this)), "Install").enabledDrag();
		hab.add(playOperation.spendAndApply(this::install));
	}

	protected void install(Flow next) {
		Runner runner = getRunner();
		setRezzed(true);
		setInstalled(true);
		defaultInstallChat();
		runner.getResources().add(this);		
		cleanupInstall(next);
	}
}

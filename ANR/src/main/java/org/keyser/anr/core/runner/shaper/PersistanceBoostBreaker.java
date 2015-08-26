package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.IceBreakerMetaCard;

/**
 * Un {@link IceBreaker} qui conserve la force
 * 
 * @author PAF
 * 
 */
public abstract class PersistanceBoostBreaker extends IceBreaker {

	protected PersistanceBoostBreaker(int id, IceBreakerMetaCard meta) {
		super(id, meta);
	}



}
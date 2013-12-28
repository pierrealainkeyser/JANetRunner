package org.keyser.anr.core.runner;

/**
 * L'evenement le Runner a piocher
 * 
 * @author PAF
 * 
 */
public class RunnerCardDrawn extends RunnerCardEvent {
	public RunnerCardDrawn(RunnerCard card) {
		super(card);
	}
}
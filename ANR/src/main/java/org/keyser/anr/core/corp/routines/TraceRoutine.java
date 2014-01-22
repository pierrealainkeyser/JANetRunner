package org.keyser.anr.core.corp.routines;

import java.util.function.BiConsumer;

import org.keyser.anr.core.Flow;
import org.keyser.anr.core.FlowArg;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.TraceAction;
import org.keyser.anr.core.corp.Routine;

/**
 * Une routine de trace
 * 
 * @author PAF
 * 
 */
public class TraceRoutine implements Routine {

	private final String text;

	private final int strength;

	private final BiConsumer<TraceAction, Flow> consumer;

	public TraceRoutine(String text, int strength, FlowArg<Flow> success) {
		this("Trace[" + strength + "] " + text, strength, (ta, next) -> {
			if (ta.isSucessful())
				success.apply(next);
			else
				next.apply();
		});
	}

	public TraceRoutine(String text, int strength, BiConsumer<TraceAction, Flow> consumer) {
		this.text = text;
		this.strength = strength;
		this.consumer = consumer;
	}

	@Override
	public void trigger(Run run, Flow next) {

		TraceAction ta = new TraceAction(text, strength, consumer);
		ta.apply(run.getGame(), next);
	}

	@Override
	public String asString() {
		return text;
	}

}

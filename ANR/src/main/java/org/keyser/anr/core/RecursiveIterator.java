package org.keyser.anr.core;

import java.util.Iterator;
import java.util.function.BiConsumer;

/**
 * Permet de gerer l'itération recursive
 * 
 * @author PAF
 * 
 * @param <T>
 */
public class RecursiveIterator<T> implements Flow {

	private final BiConsumer<T, Flow> action;

	private final Flow end;

	private final Iterator<T> it;

	public RecursiveIterator(Iterator<T> it, BiConsumer<T, Flow> action, Flow end) {
		super();
		this.it = it;
		this.action = action;
		this.end = end;
	}

	/**
	 * 
	 * @param it
	 * @param action
	 * @param end
	 */
	public static <T> void recurse(Iterator< T> it, BiConsumer<T, Flow> action, Flow end) {
		new RecursiveIterator<>((Iterator<T>) it, action, end).apply();
	}

	@Override
	public void apply() {
		if (it.hasNext()) {
			action.accept(it.next(), this);
		} else
			end.apply();
	}

}

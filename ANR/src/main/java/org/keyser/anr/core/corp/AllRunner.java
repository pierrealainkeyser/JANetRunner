package org.keyser.anr.core.corp;

import java.util.HashMap;
import java.util.Map;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.runner.Runner;
import org.keyser.anr.core.runner.RunnerCard;
import org.keyser.anr.core.runner.shapper.GordianBlade;
import org.keyser.anr.core.runner.shapper.KateMcCaffrey;

/**
 * L'index de toutes les classes
 * 
 * @author PAF
 * 
 */
public class AllRunner {

	private final Map<String, Class<? extends RunnerCard>> cards = new HashMap<>();

	private final Map<String, Class<? extends Runner>> runner = new HashMap<>();

	public AllRunner() {

		// NEUTRAL

		// SHAPER
		addRunner(KateMcCaffrey.class);
		add(GordianBlade.class);


	}

	private void add(Class<? extends RunnerCard> c) {
		cards.put(c.getAnnotation(CardDef.class).name(), c);
	}

	private void addRunner(Class<? extends Runner> c) {
		runner.put(c.getAnnotation(CardDef.class).name(), c);
	}

	/**
	 * Un nouvelle corp
	 * 
	 * @param name
	 * @return
	 */
	public Runner newRunner(String name) {
		try {
			Class<? extends Runner> c = runner.get(name);
			if (c != null)
				return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Permet de créer une instance de la carte nommée
	 * 
	 * @param name
	 * @return
	 */
	public RunnerCard newCard(String name) {

		try {
			Class<? extends RunnerCard> c = cards.get(name);
			if (c != null)
				return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return null;

	}

}

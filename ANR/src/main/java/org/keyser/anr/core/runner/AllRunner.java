package org.keyser.anr.core.runner;

import java.util.HashMap;
import java.util.Map;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.runner.neutral.Infiltration;
import org.keyser.anr.core.runner.neutral.SureGamble;
import org.keyser.anr.core.runner.shaper.AccessToGlobalsec;
import org.keyser.anr.core.runner.shaper.AesopsPawnshop;
import org.keyser.anr.core.runner.shaper.AkamatsuMemChip;
import org.keyser.anr.core.runner.shaper.ArmitageCodebusting;
import org.keyser.anr.core.runner.shaper.BatteringRam;
import org.keyser.anr.core.runner.shaper.Crypsis;
import org.keyser.anr.core.runner.shaper.Diesel;
import org.keyser.anr.core.runner.shaper.GordianBlade;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;
import org.keyser.anr.core.runner.shaper.MagnusOpus;
import org.keyser.anr.core.runner.shaper.Modded;
import org.keyser.anr.core.runner.shaper.NetShield;
import org.keyser.anr.core.runner.shaper.Pipeline;
import org.keyser.anr.core.runner.shaper.RabbitHole;
import org.keyser.anr.core.runner.shaper.SacrificialConstruct;
import org.keyser.anr.core.runner.shaper.TheMakersEye;
import org.keyser.anr.core.runner.shaper.ThePersonalTouch;
import org.keyser.anr.core.runner.shaper.TheToolbox;
import org.keyser.anr.core.runner.shaper.Tinkering;

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
		add(SureGamble.class);
		add(Infiltration.class);

		add(ArmitageCodebusting.class);
		add(AccessToGlobalsec.class);
		
		add(Crypsis.class);

		// SHAPER
		addRunner(KateMcCaffrey.class);
		add(Diesel.class);
		add(Modded.class);
		add(TheMakersEye.class);
		add(Tinkering.class);

		add(AkamatsuMemChip.class);
		add(RabbitHole.class);
		add(ThePersonalTouch.class);
		add(TheToolbox.class);

		add(AesopsPawnshop.class);
		add(SacrificialConstruct.class);

		add(BatteringRam.class);
		add(GordianBlade.class);
		add(MagnusOpus.class);
		add(NetShield.class);
		add(Pipeline.class);

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

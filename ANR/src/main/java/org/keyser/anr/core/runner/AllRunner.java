package org.keyser.anr.core.runner;

import java.util.HashMap;
import java.util.Map;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.neutral.Infiltration;
import org.keyser.anr.core.neutral.SureGamble;
import org.keyser.anr.core.runner.shapper.AccessToGlobalsec;
import org.keyser.anr.core.runner.shapper.AesopsPawnshop;
import org.keyser.anr.core.runner.shapper.AkamatsuMemChip;
import org.keyser.anr.core.runner.shapper.ArmitageCodebusting;
import org.keyser.anr.core.runner.shapper.BatteringRam;
import org.keyser.anr.core.runner.shapper.Crypsis;
import org.keyser.anr.core.runner.shapper.Diesel;
import org.keyser.anr.core.runner.shapper.GordianBlade;
import org.keyser.anr.core.runner.shapper.KateMcCaffrey;
import org.keyser.anr.core.runner.shapper.MagnusOpus;
import org.keyser.anr.core.runner.shapper.Modded;
import org.keyser.anr.core.runner.shapper.NetShield;
import org.keyser.anr.core.runner.shapper.Pipeline;
import org.keyser.anr.core.runner.shapper.RabbitHole;
import org.keyser.anr.core.runner.shapper.SacrificialConstruct;
import org.keyser.anr.core.runner.shapper.TheMakersEye;
import org.keyser.anr.core.runner.shapper.ThePersonalTouch;
import org.keyser.anr.core.runner.shapper.TheToolbox;
import org.keyser.anr.core.runner.shapper.Tinkering;

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

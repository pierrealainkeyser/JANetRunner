package org.keyser.anr.core.corp;

import java.util.HashMap;
import java.util.Map;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.corp.nbn.AnonymousTip;
import org.keyser.anr.core.corp.nbn.AstroScriptPilotProgram;
import org.keyser.anr.core.corp.nbn.BreakingNews;
import org.keyser.anr.core.corp.nbn.ClosedAccounts;
import org.keyser.anr.core.corp.nbn.DataRaven;
import org.keyser.anr.core.corp.nbn.GhostBranch;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.nbn.MatrixAnalyser;
import org.keyser.anr.core.corp.nbn.Psychographics;
import org.keyser.anr.core.corp.nbn.RedHerrings;
import org.keyser.anr.core.corp.nbn.SEASource;
import org.keyser.anr.core.corp.nbn.SanSanCityGrid;
import org.keyser.anr.core.corp.nbn.Tollbooth;
import org.keyser.anr.core.corp.neutral.Enigma;
import org.keyser.anr.core.corp.neutral.HedgeFund;
import org.keyser.anr.core.corp.neutral.Hunter;
import org.keyser.anr.core.corp.neutral.MelangeMiningCorp;
import org.keyser.anr.core.corp.neutral.PADCampaign;
import org.keyser.anr.core.corp.neutral.PriorityRequisition;
import org.keyser.anr.core.corp.neutral.PrivateSecurityForce;
import org.keyser.anr.core.corp.neutral.WallOfStatic;

/**
 * L'index de toutes les classes
 * 
 * @author PAF
 * 
 */
public class AllCorp {

	private final Map<String, Class<? extends CorpCard>> cards = new HashMap<>();

	private final Map<String, Class<? extends Corp>> corps = new HashMap<>();

	public AllCorp() {

		// NEUTRAL
		add(PriorityRequisition.class);
		add(PrivateSecurityForce.class);
		add(WallOfStatic.class);
		add(Enigma.class);
		add(Hunter.class);
		add(HedgeFund.class);
		add(MelangeMiningCorp.class);
		add(PADCampaign.class);

		// NBN
		addCorp(MakingNews.class);
		add(AstroScriptPilotProgram.class);
		add(BreakingNews.class);
		add(DataRaven.class);
		add(MatrixAnalyser.class);
		add(Tollbooth.class);
		add(AnonymousTip.class);
		add(ClosedAccounts.class);
		add(Psychographics.class);
		add(SEASource.class);
		add(GhostBranch.class);
		add(RedHerrings.class);
		add(SanSanCityGrid.class);

	}

	private void add(Class<? extends CorpCard> c) {
		cards.put(c.getAnnotation(CardDef.class).name(), c);
	}

	private void addCorp(Class<? extends Corp> c) {
		corps.put(c.getAnnotation(CardDef.class).name(), c);
	}

	/**
	 * Un nouvelle corp
	 * 
	 * @param name
	 * @return
	 */
	public Corp newCorp(String name) {
		try {
			Class<? extends Corp> c = corps.get(name);
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
	public CorpCard newCard(String name) {

		try {
			Class<? extends CorpCard> c = cards.get(name);
			if (c != null)
				return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return null;

	}

}

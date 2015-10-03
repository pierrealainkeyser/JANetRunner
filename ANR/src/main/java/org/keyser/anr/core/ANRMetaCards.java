package org.keyser.anr.core;

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
import org.keyser.anr.core.runner.neutral.AccessToGlobalsec;
import org.keyser.anr.core.runner.neutral.ArmitageCodebusting;
import org.keyser.anr.core.runner.neutral.SureGamble;
import org.keyser.anr.core.runner.shaper.AkamatsuMemChip;
import org.keyser.anr.core.runner.shaper.BatteringRam;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;
import org.keyser.anr.core.runner.shaper.NetShield;
import org.keyser.anr.core.runner.shaper.ThePersonalTouch;
import org.keyser.anr.core.runner.shaper.TheToolbox;

public class ANRMetaCards {

	public final static MetaCards INSTANCE = new MetaCards();

	static {
		// runner
		// shapper
		INSTANCE.add(KateMcCaffrey.INSTANCE);
		INSTANCE.add(AkamatsuMemChip.INSTANCE);
		INSTANCE.add(TheToolbox.INSTANCE);
		INSTANCE.add(ThePersonalTouch.INSTANCE);
		INSTANCE.add(NetShield.INSTANCE);
		
		INSTANCE.add(BatteringRam.INSTANCE);

		// neutral
		INSTANCE.add(SureGamble.INSTANCE);
		INSTANCE.add(ArmitageCodebusting.INSTANCE);
		INSTANCE.add(AccessToGlobalsec.INSTANCE);

		// corp
		// NBN
		INSTANCE.add(MakingNews.INSTANCE);
		INSTANCE.add(AstroScriptPilotProgram.INSTANCE);
		INSTANCE.add(BreakingNews.INSTANCE);
		INSTANCE.add(AnonymousTip.INSTANCE);
		INSTANCE.add(ClosedAccounts.INSTANCE);
		INSTANCE.add(SEASource.INSTANCE);
		INSTANCE.add(Psychographics.INSTANCE);
		INSTANCE.add(SanSanCityGrid.INSTANCE);
		INSTANCE.add(RedHerrings.INSTANCE);
		INSTANCE.add(GhostBranch.INSTANCE);
		INSTANCE.add(DataRaven.INSTANCE);
		INSTANCE.add(MatrixAnalyser.INSTANCE);
		INSTANCE.add(Tollbooth.INSTANCE);

		// neutral
		INSTANCE.add(PriorityRequisition.INSTANCE);
		INSTANCE.add(PrivateSecurityForce.INSTANCE);

		INSTANCE.add(PADCampaign.INSTANCE);
		INSTANCE.add(MelangeMiningCorp.INSTANCE);
		INSTANCE.add(HedgeFund.INSTANCE);

		INSTANCE.add(WallOfStatic.INSTANCE);
		INSTANCE.add(Enigma.INSTANCE);
		INSTANCE.add(Hunter.INSTANCE);
	}
}

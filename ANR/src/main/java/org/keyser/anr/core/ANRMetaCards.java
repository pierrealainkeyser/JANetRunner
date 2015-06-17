package org.keyser.anr.core;

import org.keyser.anr.core.corp.nbn.AnonymousTip;
import org.keyser.anr.core.corp.nbn.ClosedAccounts;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.nbn.Psychographics;
import org.keyser.anr.core.corp.nbn.SEASource;
import org.keyser.anr.core.corp.neutral.HedgeFund;
import org.keyser.anr.core.corp.neutral.MelangeMiningCorp;
import org.keyser.anr.core.corp.neutral.PADCampaign;
import org.keyser.anr.core.runner.neutral.ArmitageCodebusting;
import org.keyser.anr.core.runner.neutral.SureGamble;
import org.keyser.anr.core.runner.shaper.AkamatsuMemChip;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;
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

		// neutral
		INSTANCE.add(SureGamble.INSTANCE);
		INSTANCE.add(ArmitageCodebusting.INSTANCE);

		// corp
		// NBN
		INSTANCE.add(MakingNews.INSTANCE);
		INSTANCE.add(AnonymousTip.INSTANCE);
		INSTANCE.add(ClosedAccounts.INSTANCE);
		INSTANCE.add(SEASource.INSTANCE);
		INSTANCE.add(Psychographics.INSTANCE);

		// neutral
		INSTANCE.add(PADCampaign.INSTANCE);
		INSTANCE.add(MelangeMiningCorp.INSTANCE);
		INSTANCE.add(HedgeFund.INSTANCE);
	}
}

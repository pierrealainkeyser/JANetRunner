package org.keyser.anr.core.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.keyser.anr.core.EncounteredIce;

/**
 * L'analyse de tous les cout
 * @author PAF
 *
 */
public class BreakCostAnalysisCumuled {

	private final EncounteredIce ice;

	private final List<BreakCostAnalysis> analyses = new ArrayList<>();

	public BreakCostAnalysisCumuled(EncounteredIce ice) {
		this.ice = ice;
	}

	public BreakCostAnalysis find(int cardId) {
		Optional<BreakCostAnalysis> opt = analyses.stream().filter(bca -> bca.getIceBreaker().getId() == cardId).findFirst();
		return opt.get();
	}

	public void add(BreakCostAnalysis bca) {
		analyses.add(bca);
	}

	public EncounteredIce getIce() {
		return ice;
	}

	public List<BreakCostAnalysis> getAnalyses() {
		return analyses;
	}

}

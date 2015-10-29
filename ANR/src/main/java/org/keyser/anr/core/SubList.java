package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;

import org.keyser.anr.core.corp.ReadyedRoutine;

public class SubList {

	private List<ReadyedRoutine> selecteds = new ArrayList<ReadyedRoutine>();

	public void add(ReadyedRoutine sub) {
		selecteds.add(sub);
	}

	public List<ReadyedRoutine> getSelecteds() {
		return selecteds;
	}

	public int size() {
		return selecteds.size();
	}

}

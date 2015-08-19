package org.keyser.anr.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keyser.anr.core.AbstractCardEvent;
import org.keyser.anr.core.SequentialEvent;

public class CollectPossibleProgramsArea extends AbstractCardEvent implements SequentialEvent {

	private List<ProgramsArea> areas = new ArrayList<>();

	public CollectPossibleProgramsArea(Program primary) {
		super(primary, null);
	}

	public CollectPossibleProgramsArea addArea(ProgramsArea area) {
		this.areas.add(area);
		return this;
	}

	@Override
	public Program getPrimary() {
		return (Program) super.getPrimary();
	}

	public List<ProgramsArea> getAreas() {
		return Collections.unmodifiableList(areas);
	}

}

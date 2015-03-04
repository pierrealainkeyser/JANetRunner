package org.keyser.anr.core;

import java.util.List;

public class RunnerDef extends IdDef {

	private List<AbstractCardDef> resources;

	private List<AbstractCardDef> heap;

	private List<AbstractCardDef> hardwares;

	private List<AbstractCardDef> programs;

	private List<AbstractCardDef> grip;

	private List<AbstractCardDef> stack;

	public List<AbstractCardDef> getResources() {
		return resources;
	}

	public void setResources(List<AbstractCardDef> resources) {
		this.resources = resources;
	}

	public List<AbstractCardDef> getHeap() {
		return heap;
	}

	public void setHeap(List<AbstractCardDef> heap) {
		this.heap = heap;
	}

	public List<AbstractCardDef> getHardwares() {
		return hardwares;
	}

	public void setHardwares(List<AbstractCardDef> hardwares) {
		this.hardwares = hardwares;
	}

	public List<AbstractCardDef> getPrograms() {
		return programs;
	}

	public void setPrograms(List<AbstractCardDef> programs) {
		this.programs = programs;
	}

	public List<AbstractCardDef> getGrip() {
		return grip;
	}

	public void setGrip(List<AbstractCardDef> grip) {
		this.grip = grip;
	}

	public List<AbstractCardDef> getStack() {
		return stack;
	}

	public void setStack(List<AbstractCardDef> stack) {
		this.stack = stack;
	}

}

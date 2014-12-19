package org.keyser.anr.core;

import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.Program;
import org.keyser.anr.core.runner.Resource;

public class Runner extends AbstractId {

	private final AbstractCardContainer<Resource> resources = new AbstractCardContainer<>(
			CardLocation::resources);

	private final AbstractCardContainer<Program> programs = new AbstractCardContainer<>(
			CardLocation::programs);

	private final AbstractCardContainer<Hardware> hardwares = new AbstractCardContainer<>(
			CardLocation::hardwares);

	private final AbstractCardContainer<AbstractCardRunner> stack = new AbstractCardContainer<>(
			CardLocation::stack);

	private final AbstractCardContainer<AbstractCardRunner> grip = new AbstractCardContainer<>(
			CardLocation::grip);

	private final AbstractCardContainer<AbstractCardRunner> heap = new AbstractCardContainer<>(
			CardLocation::heap);

	private int link;

	protected Runner(int id, MetaCard meta) {
		super(id, meta, PlayerType.RUNNER);
	}

	@Override
	public PlayerType getOwner() {
		return PlayerType.RUNNER;
	}

	public void doDraw(int nb, Flow next) {

	}

	public AbstractCardContainer<Resource> getResources() {
		return resources;
	}

	public AbstractCardContainer<Program> getPrograms() {
		return programs;
	}

	public AbstractCardContainer<Hardware> getHardwares() {
		return hardwares;
	}

	public AbstractCardContainer<AbstractCardRunner> getStack() {
		return stack;
	}

	public AbstractCardContainer<AbstractCardRunner> getGrip() {
		return grip;
	}

	public AbstractCardContainer<AbstractCardRunner> getHeap() {
		return heap;
	}

	public void alterMemory(int delta, Flow next) {

		//TODO gestion de l'effet
	}

	public void doDamage(int damage, Flow next) {

	}

	public void alterLink(int delta) {
		setLink(getLink() + delta);
	}

	public int getLink() {
		return link;
	}

	public void setLink(int link) {
		this.link = link;
		// TODO notification effect
	}
}

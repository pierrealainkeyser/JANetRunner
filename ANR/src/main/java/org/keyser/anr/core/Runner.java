package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;

public class Runner extends AbstractId {

	private List<AbstractCardRunner> resources = new ArrayList<>();

	private List<AbstractCardRunner> programs = new ArrayList<>();

	private List<AbstractCardRunner> hardwares = new ArrayList<>();

	private List<AbstractCardRunner> stack = new ArrayList<>();

	private List<AbstractCardRunner> grip = new ArrayList<>();

	private List<AbstractCardRunner> heap = new ArrayList<>();

	protected Runner(int id, MetaCard meta) {
		super(id, meta);
	}

	public void doDraw(int nb, Flow next) {

	}

	public void doDamage(int damage, Flow next) {

	}
}

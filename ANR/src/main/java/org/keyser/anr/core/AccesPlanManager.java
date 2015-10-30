package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AccesPlanManager {

	private LinkedList<AbstractCardCorp> sequential = new LinkedList<>();

	private List<AbstractCardCorp> unordered = new LinkedList<AbstractCardCorp>();

	public void addUnordered(AbstractCardCorp card) {
		unordered.add(card);
	}

	public void addSequential(AbstractCardCorp card) {
		sequential.add(card);
	}

	public void access(AbstractCardCorp card) {
		sequential.remove(card);
		unordered.remove(card);
	}

	public int sequentialSize() {
		return sequential.size();
	}

	public List<AbstractCardCorp> getAccessible() {
		List<AbstractCardCorp> accessible = new ArrayList<>();
		accessible.addAll(unordered);
		if (!sequential.isEmpty())
			accessible.add(sequential.getFirst());

		return accessible;
	}
}

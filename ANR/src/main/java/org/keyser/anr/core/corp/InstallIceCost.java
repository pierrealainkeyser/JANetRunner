package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;

public class InstallIceCost extends InstallOn {

	private final Cost cost;

	public InstallIceCost(Integer server, Integer card, int cost) {
		this.cost = Cost.credit(cost).add(Cost.action(1));
		setServer(server);
		setCard(card);
	}

	public Cost getCost() {
		return cost;
	}

}

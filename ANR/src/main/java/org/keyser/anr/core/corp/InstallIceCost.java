package org.keyser.anr.core.corp;

public class InstallIceCost extends InstallOn {

	private int cost;

	public InstallIceCost() {
	}

	public InstallIceCost(Integer server, Integer card, int cost) {
		this.cost = cost;
		setServer(server);
		setCard(card);
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}

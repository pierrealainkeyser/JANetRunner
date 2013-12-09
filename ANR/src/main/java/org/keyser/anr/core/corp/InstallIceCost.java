package org.keyser.anr.core.corp;

public class InstallIceCost extends InstallOnServer {

	private int cost;

	public InstallIceCost() {
	}

	public InstallIceCost(int server, int cost) {
		super(server);
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}

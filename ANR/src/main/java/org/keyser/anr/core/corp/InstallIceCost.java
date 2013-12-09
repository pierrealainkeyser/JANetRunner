package org.keyser.anr.core.corp;

public class InstallIceCost {

	private int server;

	private int cost;

	public InstallIceCost() {
	}

	public InstallIceCost(int server, int cost) {
		this.server = server;
		this.cost = cost;
	}

	public int getServer() {
		return server;
	}

	public int getCost() {
		return cost;
	}

	public void setServer(int server) {
		this.server = server;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}

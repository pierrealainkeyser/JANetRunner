package org.keyser.anr.core;

public class InstallOn {

	private Integer card;

	private Integer server;

	public static InstallOn server(int server) {
		InstallOn io = new InstallOn();
		io.setServer(server);
		return io;
	}
	
	public static InstallOn card(int card) {
		InstallOn io = new InstallOn();
		io.setCard(card);
		return io;
	}

	public InstallOn() {
	}

	public Integer getCard() {
		return card;
	}

	public Integer getServer() {
		return server;
	}

	public void setCard(Integer card) {
		this.card = card;
	}

	public void setServer(Integer server) {
		this.server = server;
	}

}
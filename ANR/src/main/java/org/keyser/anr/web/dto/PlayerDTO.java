package org.keyser.anr.web.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerDTO {

	private final Map<String, Integer> wallets = new LinkedHashMap<>();

	private List<Integer> servers;

	public void setValue(String w, int amount) {
		wallets.put(w, amount);
	}

	public Map<String, Integer> getWallets() {
		return wallets;
	}

	@Override
	public String toString() {
		return "PlayerDTO [wallets=" + wallets + "]";
	}

	public List<Integer> getServers() {
		return servers;
	}

	public void setServers(List<Integer> servers) {
		this.servers = servers;
	}

}

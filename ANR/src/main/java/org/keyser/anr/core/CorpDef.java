package org.keyser.anr.core;

import java.util.List;

import org.keyser.anr.core.corp.CorpServerDef;

public class CorpDef extends IdDef {

	private List<CorpServerDef> servers;

	public List<CorpServerDef> getServers() {
		return servers;
	}

	public void setServers(List<CorpServerDef> servers) {
		this.servers = servers;
	}
}

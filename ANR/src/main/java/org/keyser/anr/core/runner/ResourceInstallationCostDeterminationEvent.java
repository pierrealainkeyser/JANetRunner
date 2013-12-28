package org.keyser.anr.core.runner;

import org.keyser.anr.core.CostDeterminationEvent;

public class ResourceInstallationCostDeterminationEvent extends CostDeterminationEvent {

	private final Resource resource;

	public ResourceInstallationCostDeterminationEvent(Resource resource) {
		super(resource.getCost());
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	

}

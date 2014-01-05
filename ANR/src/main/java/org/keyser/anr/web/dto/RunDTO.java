package org.keyser.anr.web.dto;

public class RunDTO {

	private LocationDTO target;

	private LocationDTO ice;
	
	
	//TODO rajouter la liste des routines

	private boolean done = false;

	public LocationDTO getTarget() {
		return target;
	}

	public void setTarget(LocationDTO target) {
		this.target = target;
	}

	public LocationDTO getIce() {
		return ice;
	}

	public void setIce(LocationDTO ice) {
		this.ice = ice;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}

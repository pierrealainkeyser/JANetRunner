package org.keyser.anr.web.dto;

import org.keyser.anr.web.dto.LocationDTO.ExtendedLocationDTO;

/**
 * Represente un run
 * 
 * @author PAF
 * 
 */
public class RunDTO {

	private ExtendedLocationDTO target;

	private ExtendedLocationDTO ice;

	/**
	 * La racine du serveur lorsque celle-ci est approchée
	 */
	private ExtendedLocationDTO root;

	private boolean done = false;

	public ExtendedLocationDTO getTarget() {
		return target;
	}

	public void setTarget(ExtendedLocationDTO target) {
		this.target = target;
	}

	public ExtendedLocationDTO getIce() {
		return ice;
	}

	public void setIce(ExtendedLocationDTO ice) {
		this.ice = ice;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public ExtendedLocationDTO getRoot() {
		return root;
	}

	public void setRoot(ExtendedLocationDTO root) {
		this.root = root;
	}
}

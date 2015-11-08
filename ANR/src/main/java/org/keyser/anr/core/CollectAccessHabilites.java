package org.keyser.anr.core;

/**
 * Permet de charger les habilités pour un acces de carte
 * 
 * @author PAF
 *
 */
public class CollectAccessHabilites extends CollectAbstractHabilites {

	private AbstractCardCorp acceeded;

	public CollectAccessHabilites(AbstractCardCorp acceeded) {
		this.acceeded = acceeded;
	}

	@Override
	public String toString() {
		return "CollectAccessHabilites [getFeedbacks()=" + getFeedbacks() + "]";
	}

	public AbstractCardCorp getAcceeded() {
		return acceeded;
	}

}

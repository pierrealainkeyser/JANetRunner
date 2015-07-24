package org.keyser.anr.core;

/**
 * Permet de connaitre la taille maximum de la main
 * 
 * @author pakeyser
 *
 */
public class DetermineMaxHandSizeEvent extends AbstractCardEvent implements SequentialEvent {

	private int baseSize;

	private int delta;

	public DetermineMaxHandSizeEvent(AbstractId id) {
		super(id, null);
		this.baseSize = id.getMaxHandSize();
	}

	public int computeMaxHandSize() {
		return baseSize + delta;
	}

	@Override
	public AbstractId getPrimary() {
		return (AbstractId) super.getPrimary();
	}

	public int getBaseSize() {
		return baseSize;
	}

	public void setBaseSize(int baseSize) {
		this.baseSize = baseSize;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

}

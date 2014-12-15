package org.keyser.anr.core;

public abstract class AbstractId extends AbstractCard {

	private final PlayerType playerType;

	public AbstractId(int id, MetaCard meta, PlayerType playerType) {
		super(id, meta, null, null);
		this.playerType = playerType;
	}

	/**
	 * Consommation des cout, puis appel de la fonction {@link Flow#apply()} de
	 * l'objet next
	 * 
	 * @param cost
	 * @param next
	 */
	public void spend(CostForAction cost, Flow next) {

	}

	public void draw(int i, Flow next) {

	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public boolean mayAfford(CostForAction cost) {

		// TODO implementation
		return true;
	}

}
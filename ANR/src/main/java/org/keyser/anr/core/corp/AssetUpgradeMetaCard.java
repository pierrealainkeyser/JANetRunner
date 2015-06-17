package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.AbstractCardFactory;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.MetaCard;

public class AssetUpgradeMetaCard extends MetaCard {

	private final Cost trashCost;

	public AssetUpgradeMetaCard(String name, Influence influence, Cost cost, Cost trashCost, boolean unique, String graphic, List<CardSubType> subTypes, AbstractCardFactory factory) {
		super(name, influence, cost, unique, graphic, subTypes, factory);
		this.trashCost = trashCost;
	}

	public Cost getTrashCost() {
		return trashCost;
	}

}

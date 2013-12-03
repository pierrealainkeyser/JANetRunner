package org.keyser.anr.core.corp;

import java.util.HashMap;
import java.util.Map;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.corp.nbn.DataRaven;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.nbn.MatrixAnalyser;
import org.keyser.anr.core.corp.nbn.Tollbooth;

/**
 * L'index de toutes les classes
 * 
 * @author PAF
 * 
 */
public class AllCorp {

	private final Map<String, Class<? extends CorpCard>> cards = new HashMap<>();

	private final Map<String, Class<? extends Corp>> corps = new HashMap<>();

	public AllCorp() {

		// NBN
		addCorp(MakingNews.class);
		add(DataRaven.class);
		add(MatrixAnalyser.class);
		add(Tollbooth.class);
	}

	private void add(Class<? extends CorpCard> c) {
		cards.put(c.getAnnotation(CardDef.class).name(), c);
	}

	private void addCorp(Class<? extends Corp> c) {
		corps.put(c.getAnnotation(CardDef.class).name(), c);
	}

	/**
	 * Un nouvelle corp
	 * 
	 * @param name
	 * @return
	 */
	public Corp newCorp(String name) {
		try {
			Class<? extends Corp> c = corps.get(name);
			if (c != null)
				return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Permet de créer une instance de la carte nommée
	 * 
	 * @param name
	 * @return
	 */
	public CorpCard newCard(String name) {

		try {
			Class<? extends CorpCard> c = cards.get(name);
			if (c != null)
				return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return null;

	}

}

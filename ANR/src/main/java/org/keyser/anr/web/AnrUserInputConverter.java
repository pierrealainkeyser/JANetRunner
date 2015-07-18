package org.keyser.anr.web;

import java.util.List;

import org.keyser.anr.core.AbstractCardList;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.UserInputConverter;
import org.keyser.anr.core.corp.CorpServer;

/**
 * Permet de faire la conversion des données
 * 
 * @author PAF
 *
 */
public class AnrUserInputConverter implements UserInputConverter {

	@Override
	public <T> T convert(Class<T> type, Game game, Object input) {
		if (AbstractCardList.class.equals(type)) {
			AbstractCardList acl = createAbstractCardList(game, input);
			return type.cast(acl);
		} else if (CorpServer.class.equals(type) && input instanceof Integer) {
			return type.cast(game.getCorp().getOrCreate((Integer) input));
		}

		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	private AbstractCardList createAbstractCardList(Game game, Object input) {
		AbstractCardList acl = new AbstractCardList();

		if (input instanceof List) {
			List<Integer> cards = (List<Integer>) input;
			for (Integer i : cards) {
				game.findById(i).ifPresent(acl::add);
			}

		}
		return acl;
	}

}

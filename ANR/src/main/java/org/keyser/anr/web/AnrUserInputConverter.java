package org.keyser.anr.web;

import java.util.List;
import java.util.Optional;

import org.keyser.anr.core.AbstractCardList;
import org.keyser.anr.core.EncounteredIce;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.SubList;
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
		} else if (SubList.class.equals(type)) {
			return type.cast(createSubList(game, input));
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

	@SuppressWarnings("unchecked")
	private SubList createSubList(Game game, Object input) {
		SubList acl = new SubList();

		if (input instanceof List) {
			List<Integer> subs = (List<Integer>) input;
			Optional<Run> run = game.getTurn().getRun();
			if (run.isPresent()) {
				Optional<EncounteredIce> opt = run.get().getIce();
				if (opt.isPresent()) {
					EncounteredIce ei = opt.get();

					ei.getRoutines().stream().filter(rr -> subs.contains(rr.getId())).forEach(acl::add);

				}
			}
		}
		return acl;
	}

}

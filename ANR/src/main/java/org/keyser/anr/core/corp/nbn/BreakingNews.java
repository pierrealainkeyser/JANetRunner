package org.keyser.anr.core.corp.nbn;

import java.util.Arrays;
import java.util.Optional;

import org.keyser.anr.core.CoolEffect;
import org.keyser.anr.core.EndOfTurn;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;
import org.keyser.anr.core.runner.AddTagsEvent;

public class BreakingNews extends Agenda {

	/**
	 * L'effet temporaire de rajouter des cartes
	 * @author pakeyser
	 *
	 */
	public static class BreakingNewsEffect extends CoolEffect {

		public BreakingNewsEffect(BreakingNews source) {
			super(source, EndOfTurn.class);
		}

		/**
		 * Ajout des tags au runner
		 * 
		 * @param next
		 */
		public void addTags(Flow next) {
			AddTagsEvent evt = new AddTagsEvent(source, 2);
			evt.fire(next);
		}

		@Override
		protected void uninstall(Object t, Flow next) {
			Game g = getGame();
			Runner runner = g.getRunner();

			// suppression ds tags à la fin du tour
			int tag = Math.min(2, runner.getToken(TokenType.TAG));
			runner.addToken(TokenType.TAG, -tag);

			super.uninstall(t, next);
		}
	}

	public static final MetaCard INSTANCE = new AgendaMetaCard("Breaking News", Faction.NBN.infl(1), 2, 1, false, "01082", Arrays.asList(), BreakingNews::new);

	protected BreakingNews(int id, MetaCard meta) {
		super(id, meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CoolEffect> Optional<T> createCoolEffect(Class<T> type) {

		if (BreakingNewsEffect.class.equals(type)) {
			return (Optional<T>) Optional.of(new BreakingNewsEffect(this));
		}

		return super.createCoolEffect(type);
	}

	@Override
	protected void onScored(Flow next) {
		createCoolEffect(BreakingNewsEffect.class).ifPresent(e -> e.addTags(next));
	}

}

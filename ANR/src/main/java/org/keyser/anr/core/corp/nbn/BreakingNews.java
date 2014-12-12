package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.ConfigurableEventListener;
import org.keyser.anr.core.CoolEffect;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game.CorpTurnEndedEvent;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.CorpScoreAgenda;
import org.keyser.anr.core.runner.AddTagsEvent;
import org.keyser.anr.core.runner.RunnerOld;

@CardDef(name = "Breaking News", oid = "01082")
public class BreakingNews extends Agenda {
	public BreakingNews() {
		super(Faction.NBN, 1, 2);
		add(match(CorpScoreAgenda.class).auto().pred(this::equals).async(this::sendTags));
	}

	private class TagNews extends CoolEffect {

		private TagNews(Flow next) {
			super(CorpTurnEndedEvent.class);

			new AddTagsEvent(2).fire(getGame(), next);
		}

		@Override
		public void unbind(ConfigurableEventListener conf) {

			RunnerOld r = getGame().getRunner();
			int tags = r.getTags();
			if (tags > 0) {
				int remove = Math.max(0, tags - 2);
				r.setTags(remove);
			}

			super.unbind(conf);
		}
	}

	private void sendTags(CorpScoreAgenda csa, Flow next) {
		new TagNews(next).bind(getGame());
	}
}

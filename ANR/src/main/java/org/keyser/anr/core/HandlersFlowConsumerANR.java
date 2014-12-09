package org.keyser.anr.core;

import java.util.Iterator;

/**
 * TODO à déplacer dans le jeu
 * @author pakeyser
 *
 */
public class HandlersFlowConsumerANR implements HandlersFlowConsumer {

	private static class SequenceMatcher implements Flow {

		private final EventMatchersFlow<Object> flow;

		public SequenceMatcher(EventMatchersFlow<Object> flow) {
			super();
			this.flow = flow;
			it = flow.getMatchers().iterator();
		}

		@Override
		public void apply() {
			if (it.hasNext()) {
				EventConsumer<Object> ec = it.next();
				ec.apply(flow.getEvent(), this);
			} else {
				this.flow.apply();
			}
		}

		private final Iterator<EventMatcher<Object>> it;
	}

	@Override
	public void apply(EventMatchersFlow<Object> flow) {
		Object o = flow.getEvent();
		if (o instanceof CollectibleEvent) {

			SequenceMatcher sm = new SequenceMatcher(flow);
			sm.apply();
		}
		else{
			handleANRSpecifics(flow);
		}
	}
	
	/**
	 * Il faut créer 2 ensembles : un pour le joueur actif, l'autre pour le joueur inactif puis demander pour chacun des ensembles l'ordre
	 * @param flow
	 */
	private void handleANRSpecifics(EventMatchersFlow<Object> flow){
		
	}

}

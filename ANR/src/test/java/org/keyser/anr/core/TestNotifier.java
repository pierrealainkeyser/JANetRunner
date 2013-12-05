package org.keyser.anr.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestNotifier implements Notifier {

	private final static Logger logger = LoggerFactory.getLogger(TestNotifier.class);

	private Question question;

	@Override
	public void notification(Notification notif) {
		logger.info("{}", notif);
		if (notif instanceof Question) {
			this.question = (Question) notif;
		}
	}

	public Response find(String option) {
		if (question == null)
			throw new IllegalStateException("pas de question");

		return question.find(option);
	}

}

package org.keyser.anr.web;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Permet de gerer les files d'attente par jeu
 * 
 * @author PAF
 * 
 */
public class MailboxEndpointProcessor implements EndpointProcessor {

	private static class InputMessageWithEndPoint {
		private final Endpoint endpoint;

		private final InputMessage input;

		private InputMessageWithEndPoint(InputMessage input, Endpoint endpoint) {
			this.input = input;
			this.endpoint = endpoint;
		};

		public void run() {
			input.apply(endpoint);
		}
	}

	private final Executor executor;

	private Lock mutex = new ReentrantLock();

	private LinkedList<InputMessageWithEndPoint> runs = new LinkedList<>();

	public MailboxEndpointProcessor(Executor executor) {
		this.executor = executor;
	}

	@Override
	public void process(InputMessage input, Endpoint endpoint) {

		// si la queue
		boolean empty = false;
		try {
			mutex.lock();

			// si la file est vide c'est qu'aucun thread ne la consomme
			empty = runs.isEmpty();
			runs.add(new InputMessageWithEndPoint(input, endpoint));
		} finally {
			mutex.unlock();
		}

		if (empty) {
			executor.execute(this::processQueue);
		}
	}

	/**
	 * Permet de faire un polling sur la queue
	 */
	private void processQueue() {

		boolean running = true;
		while (running) {

			InputMessageWithEndPoint ir = null;

			// acces au premier élèment
			try {
				mutex.lock();
				ir = runs.peek();
			} finally {
				mutex.unlock();
			}

			if (ir != null) {
				// on transmet
				try {
					ir.run();
				} catch (Exception e) {
					// TODO gestion de l'erreur
				} finally {

					// on dépile apres la transmission
					try {
						mutex.lock();
						runs.poll();
						// on s'arrete des que la queue est vide
						running = !runs.isEmpty();
					} finally {
						mutex.unlock();
					}
				}
			}
		}

	}
}

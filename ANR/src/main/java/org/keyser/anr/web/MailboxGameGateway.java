package org.keyser.anr.web;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MailboxGameGateway implements GameGateway {

	private final GameGateway delegated;

	private final Executor executor;

	/**
	 * Une structure pour stocker les parametres
	 * 
	 * @author PAF
	 * 
	 */
	private class IncommingRunnable {

		private final GameOutput output;
		private final Object incomming;

		private IncommingRunnable(GameOutput output, Object incomming) {
			this.output = output;
			this.incomming = incomming;
		}
	}

	private LinkedList<IncommingRunnable> runs = new LinkedList<>();

	/**
	 * Permet de faire un polling sur la queue
	 */
	private void processQueue() {

		boolean running = true;
		while (running) {

			IncommingRunnable ir = null;

			// acces au premier élément
			try {
				mutex.lock();
				ir = runs.peek();
			} finally {
				mutex.unlock();
			}

			if (ir != null) {
				// on transmet
				try {
					delegated.accept(ir.output, ir.incomming);
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

	private Lock mutex = new ReentrantLock();

	public MailboxGameGateway(GameGateway delegated, Executor executor) {
		this.delegated = delegated;
		this.executor = executor;
	}

	@Override
	public void accept(GameOutput output, Object incomming) {

		// si la queue
		boolean empty = false;
		try {
			mutex.lock();

			// si la file est vide c'est qu'aucun thread ne la consomme
			empty = runs.isEmpty();
			runs.add(new IncommingRunnable(output, incomming));
		} finally {
			mutex.unlock();
		}

		if (empty) {
			executor.execute(this::processQueue);
		}

	}

	@Override
	public void register(GameOutput output) {
		delegated.register(output);

	}

	@Override
	public void remove(GameOutput output) {
		delegated.remove(output);

	}

}

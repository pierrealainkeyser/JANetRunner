package org.keyser.anr.core;

/**
 * Un evenement
 * 
 * @author PAF
 *
 */
public class ChatEvent implements SequentialEvent {

	private final String msg;

	public ChatEvent(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}
}

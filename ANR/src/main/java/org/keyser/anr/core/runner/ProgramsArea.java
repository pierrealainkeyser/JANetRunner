package org.keyser.anr.core.runner;

import java.util.Optional;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.Flow;

public interface ProgramsArea {

	/**
	 * Permet d'installer un nouveau program. Gère également la mémoire
	 * 
	 * @param program
	 * @param next
	 */
	public void installProgram(Program program, Flow next);

	/**
	 * Permet de gérer la mémoire. De voir s'il faut supprimer
	 * 
	 * @param next
	 */
	public void runMemoryCheck(Flow next);

	/**
	 * Renvoi la carte associée à la zone
	 * 
	 * @return
	 */
	public default Optional<AbstractCard> getProgramsHost() {
		return Optional.empty();
	}
}

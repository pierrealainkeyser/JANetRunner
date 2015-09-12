package org.keyser.anr.core.runner;

import java.util.Optional;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.Flow;

public interface ProgramsArea {

	/**
	 * Permet d'installer un nouveau program. G�re �galement la m�moire
	 * 
	 * @param program
	 * @param next
	 */
	public void installProgram(Program program, Flow next);

	/**
	 * Permet de gérer la mémoire. En excluant le program installe
	 * 
	 * 
	 * @param next
	 * @param justInstalled
	 */
	public void runMemoryCheck(Flow next, Optional<Program> justInstalled);

	/**
	 * Gestion de mémoire
	 * 
	 * @param next
	 */
	public default void runMemoryCheck(Flow next) {
		runMemoryCheck(next, Optional.empty());
	}

	/**
	 * Renvoi la carte associée la zone
	 * 
	 * @return
	 */
	public default Optional<AbstractCard> getProgramsHost() {
		return Optional.empty();
	}
}

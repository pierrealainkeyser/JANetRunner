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
	 * Permet de g�rer la m�moire. De voir s'il faut supprimer
	 * 
	 * @param next
	 */
	public void runMemoryCheck(Flow next);

	/**
	 * Renvoi la carte associ�e � la zone
	 * 
	 * @return
	 */
	public default Optional<AbstractCard> getProgramsHost() {
		return Optional.empty();
	}
}
